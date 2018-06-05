package co.cubbard.video;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;


public class YouTubeSearch implements VideoSearch
{	
	private static final Properties properties = new Properties();
	private static YouTube youtube;
	
	static {
		youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
			@Override
			public void initialize(HttpRequest request) throws IOException {}
		}).setApplicationName("WebHost").build();		
	}

	private String youtubeApiKey;
	private String youtubeBaseUrl;
		
	
	public YouTubeSearch(String youtubeApiKey, String youtubeBaseUrl) {
		this.youtubeApiKey  = youtubeApiKey;
		this.youtubeBaseUrl = youtubeBaseUrl;
	}
	
	private YouTube.Search.List defaultSearch() throws IOException {
		// Define Search API Request
		YouTube.Search.List search = youtube.search().list("id,snippet");
		
		// API key from properties file
		search.setKey(youtubeApiKey);
		search.setType("video");
		search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
		search.setMaxResults(10l);
		search.setVideoDuration("medium");
		
		return search;
	}
	
	private List<SearchResult> resultsForKeyword(String keyword) throws IOException {
		YouTube.Search.List search = defaultSearch();
		search.setQ(keyword);
		return search.execute().getItems();
	}
	
	public List<Title> search(String keyword) {
		try {
			List<SearchResult> resultList = resultsForKeyword(keyword);
			return resultList.stream().map(new Function<SearchResult, Title>() {
				@Override
				public Title apply(SearchResult sr) {
					return new Title(sr.getSnippet().getTitle(), youtubeBaseUrl + "?v=" + sr.getId().getVideoId(), keyword);
				}
			}).collect(Collectors.toList());
		} catch (IOException e) {
			throw new RuntimeException("Failed to fetch results [keyword=" + keyword + "]", e);
		}
	}
}
