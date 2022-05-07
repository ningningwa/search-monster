package searchengine;

public class Product {
	String title;
	String link;
	String price;
	String image;
	
	public Product(String title, String link, String price, String image) {
		this.title = title;
		this.link = link;
		this.price = price;
		this.image = image;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public String toString() {
		return "Product [title=" + title + ", link=" + link + ", price=" + price + ", image=" + image + "]";
	}
}
