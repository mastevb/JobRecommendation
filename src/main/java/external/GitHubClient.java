package external;

import entity.Item;
import entity.Item.ItemBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.module.ModuleDescriptor.Builder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class GitHubClient {

	private static final String URL_TEMPLATE = "https://jobs.github.com/positions.json?description=%s&lat=%s&long=%s";

	private static final String DEFAULT_KEYWORD = "developer";

	public List<Item> search(double lat, double lon, String keyword) {
		// preparation
		// If keyword is null, set the keyword to default
		if (keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}
		try {
			// encode the string to UTF-8 so it will be ready to be inserted into the
			// URL_TEMPLATE
			// (ex. space)
			keyword = URLEncoder.encode(keyword, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String url = String.format(URL_TEMPLATE, keyword, lat, lon);
		CloseableHttpClient httpclient = HttpClients.createDefault();
		// Create a custom response handler
		ResponseHandler<List<Item>> responseHandler = new ResponseHandler<List<Item>>() {
			@Override
			public List<Item> handleResponse(final HttpResponse response) throws IOException {
				// only returns 200 when successful -> getting resource
				if (response.getStatusLine().getStatusCode() != 200) {
					// returns an empty JSONArray when request is unsuccessful
					return new ArrayList<>();
				}
				HttpEntity entity = response.getEntity();
				if (entity == null) {
					return new ArrayList<>();
				}
				// getting the response body of the request and cast to string
				String responseBody = EntityUtils.toString(entity);
				// cast to JSONArray and return
				JSONArray array = new JSONArray(responseBody);
				return getItemList(array);
			}
		};
		// execution
		try {
			return httpclient.execute(new HttpGet(url), responseHandler);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// returns empty if exception raised
		return new ArrayList<>();
	}

	// helper function for casting JSONArray to list of items
	private List<Item> getItemList(JSONArray array) {
		List<Item> itemList = new ArrayList<>();
		List<String> descriptionList = new ArrayList<>();
		for (int i = 0; i < array.length(); i++) {
			// We need to extract keywords from description since GitHub API
			// doesn't return keywords.
			String description = getStringFieldOrEmpty(array.getJSONObject(i), "description");
			if (description.equals("") || description.equals("\n")) {
				descriptionList.add(getStringFieldOrEmpty(array.getJSONObject(i), "title"));
			} else {
				descriptionList.add(description);
			}
		}
		// We need to get keywords from multiple text in one request since
		// MonkeyLearnAPI has limitations on request per minute.
		List<List<String>> keywords = MonkeyLearnClient
				.extractKeywords(descriptionList.toArray(new String[descriptionList.size()]));
		for (int i = 0; i < array.length(); i++) {
			// object -> item
			// item -> add to itemList
			JSONObject object = array.getJSONObject(i);
			ItemBuilder builder = new ItemBuilder();
			// setting builder
			builder.setItemId(getStringFieldOrEmpty(object, "id"));
			builder.setName(getStringFieldOrEmpty(object, "title"));
			builder.setAddress(getStringFieldOrEmpty(object, "location"));
			builder.setUrl(getStringFieldOrEmpty(object, "url"));
			builder.setImageUrl(getStringFieldOrEmpty(object, "company_logo"));
			builder.setKeywords(new HashSet<String>(keywords.get(i)));
			// create Item from builder
			Item item = builder.build();
			itemList.add(item);
		}
		return itemList;
	}

	// returns the value of the corresponding key in the JSONObject
	// returns an empty String if the key does not exist
	private String getStringFieldOrEmpty(JSONObject obj, String field) {
		return obj.isNull(field) ? "" : obj.getString(field);
	}

}
