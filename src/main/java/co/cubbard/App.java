package co.cubbard;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import co.cubbard.util.CubbardFileHandle;
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
		String targetPath = String.format("%s/%s/%s.mp4", properties.getProperty("targetPath"), title.getKeyword(), LocalDateTime.now());
		strategy.download(title, new File(targetPath));
	}
	
	public static void main(String...args) {
		VideoSearch search = new YouTubeSearch(
			properties.getProperty("youtube.apiKey"),
			properties.getProperty("youtube.url.base")
		);
		
		try {
			List<String> keywords = CubbardFileHandle.getKeywords();
			for (String keyword : keywords) {
				List<Title> titles = getTitles(search, keyword);
				int index = new Random().nextInt(titles.size());
				download(new PickVideoDownload(properties.getProperty("pickvideo.url.base")), getTitles(search, keyword).get(Math.abs(index)));
			}
		} catch (IOException e) {
			System.out.println("Could not ready keywords file! " + e.getMessage());;
		}
	}
}
