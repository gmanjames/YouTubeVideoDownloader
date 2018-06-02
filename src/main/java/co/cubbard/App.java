package co.cubbard;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import co.cubbard.video.Download;
import co.cubbard.video.PickVideoDownload;
import co.cubbard.video.Title;
import co.cubbard.video.VideoSearch;
import co.cubbard.video.YouTubeSearch;

public class App
{
	private static final Properties properties = new Properties();
	
	static {
		loadProperties(properties);
	}
	
	
	private static void loadProperties(Properties props) {
		try {
			InputStream in = YouTubeSearch.class.getResourceAsStream("/app.properties");
			properties.load(in);
		} catch (IOException e) {
			throw new RuntimeException("Unable to read properties file!");
		}
	}
	
	private static List<Title> getTitles(VideoSearch vs, String keyword) {
		return vs.search(keyword);
	}
	
	private static void download(Download strategy, Title title) {
		strategy.download(title, new File(properties.getProperty("targetPath") + title.getTitle() + ".mp4"));
	}
	
	public static void main(String...args) {
		String keyword = args[0];
		List<Title> titles = getTitles(
				new YouTubeSearch(
					properties.getProperty("youtube.apiKey"),
					properties.getProperty("youtube.url.base")
				), keyword);
		
		// Download the first title for a test
		int index = new Random().nextInt() % titles.size();
		download(new PickVideoDownload(properties.getProperty("pickvideo.url.base")), titles.get(index));
	}
}
