package co.cubbard.video;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Title
{
	private String title;
	private String url;
	private String keyword;
	
	public Title() {}
	
	public Title(String title, String url) {
		super();
		this.title = title;
		this.url = url;
	}
	
	public Title(String title, String url, String keyword) {
		this(title, url);
		this.keyword = keyword;
	}
}
