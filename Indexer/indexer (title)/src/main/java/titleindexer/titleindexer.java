package titleindexer;

import java.util.HashSet;
import java.util.Set;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.tartarus.snowball.ext.englishStemmer;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import scala.Tuple2;
public final class titleindexer {

    static int id = 0;

    public static void main(String[] args) throws Exception {


        SparkSession spark = SparkSession
                .builder()
                .appName("Title Inverted Indexer")
                .getOrCreate();

 //       SparkSession spark = SparkSession
 //               .builder()
 //               .appName("Inverted Indexer")
                //.master("local[5]")
 //               .config("spark.master", "local[2]")
 //               .getOrCreate();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String url = "jdbc:mysql://cis555final.cru751xzhha3.us-east-2.rds.amazonaws.com:3306/documents?user=cis555&password=cis555final";

        Dataset<Row> docDF = spark.read()
                .format("jdbc")
                .option("url", url)
                .option("driver", "com.mysql.cj.jdbc.Driver")
                .option("dbtable", "t_document")
                .load().repartition(5);

        Set<String> stopWords = getStopWords();

        JavaRDD<Row> docsRDD = docDF.toJavaRDD();

        JavaPairRDD<String, String> url2Doccontent =
                docsRDD.mapToPair(row -> new Tuple2<>(row.getAs("access_url"), row.getAs("html")));

        JavaPairRDD<Tuple2<Tuple2<String, String>, Integer>, Tuple2<String, Double>> pairCounts = url2Doccontent.flatMapToPair(pair -> {
            ObjectArrayList<Tuple2<Tuple2<Tuple2<String, String>, Integer>, Tuple2<String, Double>>> tuples = new ObjectArrayList<>();

            Object2IntOpenHashMap<String> term2Count = new Object2IntOpenHashMap<>();
            Object2IntOpenHashMap<String> term2loc = new Object2IntOpenHashMap<>();

            englishStemmer stemmer = new englishStemmer();
            int maxCount = 0;

            Document doc = Jsoup.parse(pair._2);
            String[] oWords = doc.title().split("[\\p{Punct}\\s]+");

            String title = doc.title();
            int loc = 0;

            for (String oWord : oWords) {
                String word = oWord.toLowerCase()
                        .replaceAll("[^\\x00-\\x7F]", "")
                        .replaceAll("\u0000", "")
                        .trim();

                if (word!=null && word.length() < 20) {
                    if (!word.isEmpty() && !stopWords.contains(word)) {
                        stemmer.setCurrent(word);
                        if (stemmer.stem()) {
                            word = stemmer.getCurrent();
                        }
                    }
                    if (!word.isEmpty() && !stopWords.contains(word)) {
                        int count = term2Count.containsKey(word) ? term2Count.getInt(word) + 1 : 1;
                        term2Count.put(word, count);
                        maxCount=Math.max(count, maxCount);
                        if(!term2loc.containsKey(word)){
                            term2loc.put(word,loc);
                        }
                    }
                }
                loc++;
            }

            for (String term : term2Count.keySet()) {
                // Normalize term frequency by maximum term frequency in document
                tuples.add(new Tuple2<>(new Tuple2<>(new Tuple2<>(title,term), term2loc.getInt(term)),
                        new Tuple2<>(pair._1, 0.5 + (1 - 0.5) * ((double) term2Count.getInt(term) / term2Count.keySet().size()))));
            }

            return tuples.iterator();
        });

        JavaRDD<titleEntry> entries = pairCounts.map(pair -> {
            titleEntry entry = new titleEntry();
            id++;
            entry.setId(id);
            entry.setTerm(pair._1._1._2);
            entry.setLoc(pair._1._2);
            entry.setTitle(pair._1._1._1);
            entry.setUrl(pair._2._1);
            entry.setWeight(pair._2._2);
            return entry;
        });

        Dataset<Row> invertedIndexDF = spark.createDataFrame(entries, titleEntry.class);

        System.out.println("Writing to inverted_index");

        invertedIndexDF.write()
                .format("jdbc")
                .option("url", url)
                .option("driver", "com.mysql.cj.jdbc.Driver")
                .option("dbtable", "title_invertedindex")
                .option("truncate", true)
                .mode("overwrite")
                .save();

        System.out.println("Finished writing to inverted_index");
        spark.close();
    }


    private static Set<String> getStopWords() {
        String input = "a\n"
                + "able\n"
                + "about\n"
                + "above\n"
                + "according\n"
                + "accordingly\n"
                + "across\n"
                + "actually\n"
                + "after\n"
                + "afterwards\n"
                + "again\n"
                + "against\n"
                + "all\n"
                + "allow\n"
                + "allows\n"
                + "almost\n"
                + "alone\n"
                + "along\n"
                + "already\n"
                + "also\n"
                + "although\n"
                + "always\n"
                + "am\n"
                + "among\n"
                + "amongst\n"
                + "an\n"
                + "and\n"
                + "another\n"
                + "any\n"
                + "anybody\n"
                + "anyhow\n"
                + "anyone\n"
                + "anything\n"
                + "anyway\n"
                + "anyways\n"
                + "anywhere\n"
                + "apart\n"
                + "appear\n"
                + "appreciate\n"
                + "appropriate\n"
                + "are\n"
                + "aren't\n"
                + "around\n"
                + "as\n"
                + "aside\n"
                + "ask\n"
                + "asking\n"
                + "associated\n"
                + "at\n"
                + "available\n"
                + "away\n"
                + "awfully\n"
                + "b\n"
                + "back\n"
                + "be\n"
                + "became\n"
                + "because\n"
                + "become\n"
                + "becomes\n"
                + "becoming\n"
                + "been\n"
                + "before\n"
                + "beforehand\n"
                + "behind\n"
                + "being\n"
                + "believe\n"
                + "below\n"
                + "beside\n"
                + "besides\n"
                + "best\n"
                + "better\n"
                + "between\n"
                + "beyond\n"
                + "both\n"
                + "brief\n"
                + "but\n"
                + "by\n"
                + "c\n"
                + "came\n"
                + "can\n"
                + "cannot\n"
                + "cant\n"
                + "can't\n"
                + "cause\n"
                + "causes\n"
                + "certain\n"
                + "certainly\n"
                + "changes\n"
                + "clearly\n"
                + "co\n"
                + "com\n"
                + "come\n"
                + "comes\n"
                + "concerning\n"
                + "consequently\n"
                + "consider\n"
                + "considering\n"
                + "contain\n"
                + "containing\n"
                + "contains\n"
                + "corresponding\n"
                + "could\n"
                + "couldn't\n"
                + "course\n"
                + "currently\n"
                + "d\n"
                + "dear\n"
                + "definitely\n"
                + "described\n"
                + "despite\n"
                + "did\n"
                + "didn't\n"
                + "different\n"
                + "do\n"
                + "does\n"
                + "doesn't\n"
                + "doing\n"
                + "done\n"
                + "don't\n"
                + "down\n"
                + "downwards\n"
                + "during\n"
                + "e\n"
                + "each\n"
                + "edu\n"
                + "eg\n"
                + "eight\n"
                + "either\n"
                + "else\n"
                + "elsewhere\n"
                + "enough\n"
                + "entirely\n"
                + "especially\n"
                + "et\n"
                + "etc\n"
                + "even\n"
                + "ever\n"
                + "every\n"
                + "everybody\n"
                + "everyone\n"
                + "everything\n"
                + "everywhere\n"
                + "ex\n"
                + "exactly\n"
                + "example\n"
                + "except\n"
                + "f\n"
                + "far\n"
                + "few\n"
                + "fifth\n"
                + "first\n"
                + "five\n"
                + "followed\n"
                + "following\n"
                + "follows\n"
                + "for\n"
                + "former\n"
                + "formerly\n"
                + "forth\n"
                + "four\n"
                + "from\n"
                + "further\n"
                + "furthermore\n"
                + "g\n"
                + "get\n"
                + "gets\n"
                + "getting\n"
                + "given\n"
                + "gives\n"
                + "go\n"
                + "goes\n"
                + "going\n"
                + "gone\n"
                + "got\n"
                + "gotten\n"
                + "greetings\n"
                + "h\n"
                + "had\n"
                + "hadn't\n"
                + "happens\n"
                + "hardly\n"
                + "has\n"
                + "hasn't\n"
                + "have\n"
                + "haven't\n"
                + "having\n"
                + "he\n"
                + "he'd\n"
                + "he'll\n"
                + "hello\n"
                + "help\n"
                + "hence\n"
                + "her\n"
                + "here\n"
                + "hereafter\n"
                + "hereby\n"
                + "herein\n"
                + "here's\n"
                + "hereupon\n"
                + "hers\n"
                + "herself\n"
                + "he's\n"
                + "hi\n"
                + "high\n"
                + "him\n"
                + "himself\n"
                + "his\n"
                + "hither\n"
                + "hopefully\n"
                + "how\n"
                + "howbeit\n"
                + "however\n"
                + "how's\n"
                + "i\n"
                + "i'd\n"
                + "ie\n"
                + "if\n"
                + "ignored\n"
                + "i'll\n"
                + "i'm\n"
                + "immediate\n"
                + "in\n"
                + "inasmuch\n"
                + "inc\n"
                + "indeed\n"
                + "indicate\n"
                + "indicated\n"
                + "indicates\n"
                + "inner\n"
                + "insofar\n"
                + "instead\n"
                + "into\n"
                + "inward\n"
                + "is\n"
                + "isn't\n"
                + "it\n"
                + "its\n"
                + "it's\n"
                + "itself\n"
                + "i've\n"
                + "j\n"
                + "just\n"
                + "k\n"
                + "keep\n"
                + "keeps\n"
                + "kept\n"
                + "know\n"
                + "known\n"
                + "knows\n"
                + "l\n"
                + "last\n"
                + "lately\n"
                + "later\n"
                + "latter\n"
                + "latterly\n"
                + "least\n"
                + "less\n"
                + "lest\n"
                + "let\n"
                + "let's\n"
                + "like\n"
                + "liked\n"
                + "likely\n"
                + "little\n"
                + "long\n"
                + "look\n"
                + "looking\n"
                + "looks\n"
                + "ltd\n"
                + "m\n"
                + "made\n"
                + "mainly\n"
                + "make\n"
                + "many\n"
                + "may\n"
                + "maybe\n"
                + "me\n"
                + "mean\n"
                + "meanwhile\n"
                + "merely\n"
                + "might\n"
                + "more\n"
                + "moreover\n"
                + "most\n"
                + "mostly\n"
                + "much\n"
                + "must\n"
                + "mustn't\n"
                + "my\n"
                + "myself\n"
                + "n\n"
                + "name\n"
                + "namely\n"
                + "nd\n"
                + "near\n"
                + "nearly\n"
                + "necessary\n"
                + "need\n"
                + "needs\n"
                + "neither\n"
                + "never\n"
                + "nevertheless\n"
                + "new\n"
                + "next\n"
                + "nine\n"
                + "no\n"
                + "nobody\n"
                + "non\n"
                + "none\n"
                + "noone\n"
                + "nor\n"
                + "normally\n"
                + "not\n"
                + "nothing\n"
                + "novel\n"
                + "now\n"
                + "nowhere\n"
                + "o\n"
                + "obviously\n"
                + "of\n"
                + "off\n"
                + "often\n"
                + "oh\n"
                + "ok\n"
                + "okay\n"
                + "old\n"
                + "on\n"
                + "once\n"
                + "one\n"
                + "ones\n"
                + "only\n"
                + "onto\n"
                + "or\n"
                + "other\n"
                + "others\n"
                + "otherwise\n"
                + "ought\n"
                + "our\n"
                + "ours\n"
                + "ourselves\n"
                + "out\n"
                + "outside\n"
                + "over\n"
                + "overall\n"
                + "own\n"
                + "p\n"
                + "particular\n"
                + "particularly\n"
                + "per\n"
                + "perhaps\n"
                + "placed\n"
                + "please\n"
                + "plus\n"
                + "possible\n"
                + "presumably\n"
                + "probably\n"
                + "provides\n"
                + "put\n"
                + "q\n"
                + "que\n"
                + "quite\n"
                + "qv\n"
                + "r\n"
                + "rather\n"
                + "rd\n"
                + "re\n"
                + "really\n"
                + "reasonably\n"
                + "regarding\n"
                + "regardless\n"
                + "regards\n"
                + "relatively\n"
                + "respectively\n"
                + "right\n"
                + "s\n"
                + "said\n"
                + "same\n"
                + "saw\n"
                + "say\n"
                + "saying\n"
                + "says\n"
                + "second\n"
                + "secondly\n"
                + "see\n"
                + "seeing\n"
                + "seem\n"
                + "seemed\n"
                + "seeming\n"
                + "seems\n"
                + "seen\n"
                + "self\n"
                + "selves\n"
                + "sensible\n"
                + "sent\n"
                + "serious\n"
                + "seriously\n"
                + "seven\n"
                + "several\n"
                + "shall\n"
                + "shan't\n"
                + "she\n"
                + "she'd\n"
                + "she'll\n"
                + "she's\n"
                + "should\n"
                + "shouldn't\n"
                + "since\n"
                + "six\n"
                + "so\n"
                + "some\n"
                + "somebody\n"
                + "somehow\n"
                + "someone\n"
                + "something\n"
                + "sometime\n"
                + "sometimes\n"
                + "somewhat\n"
                + "somewhere\n"
                + "soon\n"
                + "sorry\n"
                + "specified\n"
                + "specify\n"
                + "specifying\n"
                + "still\n"
                + "sub\n"
                + "such\n"
                + "sup\n"
                + "sure\n"
                + "t\n"
                + "take\n"
                + "taken\n"
                + "tell\n"
                + "tends\n"
                + "th\n"
                + "than\n"
                + "thank\n"
                + "thanks\n"
                + "thanx\n"
                + "that\n"
                + "thats\n"
                + "that's\n"
                + "the\n"
                + "their\n"
                + "theirs\n"
                + "them\n"
                + "themselves\n"
                + "then\n"
                + "thence\n"
                + "there\n"
                + "thereafter\n"
                + "thereby\n"
                + "therefore\n"
                + "therein\n"
                + "theres\n"
                + "there's\n"
                + "thereupon\n"
                + "these\n"
                + "they\n"
                + "they'd\n"
                + "they'll\n"
                + "they're\n"
                + "they've\n"
                + "think\n"
                + "third\n"
                + "this\n"
                + "thorough\n"
                + "thoroughly\n"
                + "those\n"
                + "though\n"
                + "three\n"
                + "through\n"
                + "throughout\n"
                + "thru\n"
                + "thus\n"
                + "tis\n"
                + "to\n"
                + "together\n"
                + "too\n"
                + "took\n"
                + "toward\n"
                + "towards\n"
                + "tried\n"
                + "tries\n"
                + "truly\n"
                + "try\n"
                + "trying\n"
                + "twas\n"
                + "twice\n"
                + "two\n"
                + "u\n"
                + "un\n"
                + "under\n"
                + "unfortunately\n"
                + "unless\n"
                + "unlikely\n"
                + "until\n"
                + "unto\n"
                + "up\n"
                + "upon\n"
                + "us\n"
                + "use\n"
                + "used\n"
                + "useful\n"
                + "uses\n"
                + "using\n"
                + "usually\n"
                + "uucp\n"
                + "v\n"
                + "value\n"
                + "various\n"
                + "very\n"
                + "via\n"
                + "viz\n"
                + "vs\n"
                + "w\n"
                + "want\n"
                + "wants\n"
                + "was\n"
                + "wasn't\n"
                + "way\n"
                + "we\n"
                + "we'd\n"
                + "welcome\n"
                + "well\n"
                + "we'll\n"
                + "went\n"
                + "were\n"
                + "we're\n"
                + "weren't\n"
                + "we've\n"
                + "what\n"
                + "whatever\n"
                + "what's\n"
                + "when\n"
                + "whence\n"
                + "whenever\n"
                + "when's\n"
                + "where\n"
                + "whereafter\n"
                + "whereas\n"
                + "whereby\n"
                + "wherein\n"
                + "where's\n"
                + "whereupon\n"
                + "wherever\n"
                + "whether\n"
                + "which\n"
                + "while\n"
                + "whither\n"
                + "who\n"
                + "whoever\n"
                + "whole\n"
                + "whom\n"
                + "who's\n"
                + "whose\n"
                + "why\n"
                + "why's\n"
                + "will\n"
                + "willing\n"
                + "wish\n"
                + "with\n"
                + "within\n"
                + "without\n"
                + "wonder\n"
                + "won't\n"
                + "would\n"
                + "wouldn't\n"
                + "x\n"
                + "y\n"
                + "yes\n"
                + "yet\n"
                + "you\n"
                + "you'd\n"
                + "you'll\n"
                + "your\n"
                + "you're\n"
                + "yours\n"
                + "yourself\n"
                + "yourselves\n"
                + "you've\n"
                + "z\n";

        Set<String> stopWords = new HashSet<String>();
        for (String stopWord: input.split("\n")) {
            stopWords.add(stopWord);
        }
        return stopWords;
    }

}