Group 17
Team Name: JustPass

Full name & SEAS login of team members:
Zhonghao Lian (zhlian)
Ning Wan (ningwan)
Xincheng Zhu （kyriezxc）
Haoran Liu


Which features did you implement? 
  (list features, or write 'Entire assignment')
Crawler
Indexer
PageRank
SearchEngine
WebInterface

Did you complete any extra-credit tasks? If so, which ones?
  (list extra-credit tasks)
No.

Any special instructions for building and running your solution?
  (include detailed instructions, or write 'None')

Search Engine: enters the search_engine folder, run 'mvn exec:java@search-engine'
Web Interface: enters the web_interface folder, run 'npm install' & 'npm start'

PageRank: Run "mvn package" to generate PageRank jar file and then deploy it on Spark EMR with arguement "--class PageRank".

Indexer: Same as PageRank but with arguement "--class bodyindexer.indexer" for indexer (body) and "--class titleindexer.indexer" for indexer (title).

Crawler: Run CrawlerTopology.java under crawler folder in order to crawl url to local redis database (change the root url you want to crawl if you want). This will save url/html and relationships into redis. Then, run WebcrawlerApplication.java under webcrawler folder and run "http://localhost:9527/document/saveAll" in browser to upload from local database(redis) to aws rds. (Redis should be installed.)

Did you personally write _all_ the code you are submitting
(other than code from the course web page)?
  [X] Yes
  [ ] No

Did you copy any code from the Internet, or from classmates?
  [ ] Yes
  [X] No

Did you collaborate with anyone on this assignment?
  [ ] Yes
  [X] No
