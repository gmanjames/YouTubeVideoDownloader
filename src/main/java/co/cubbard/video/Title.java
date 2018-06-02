package co.cubbard.video;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Title
{
	private String title;
	private String url;
	
	public Title() {}
	
	public Title(String title, String url) {
		super();
		this.title = title;
		this.url = url;
	}
}
