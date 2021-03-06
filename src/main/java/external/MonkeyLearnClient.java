package external;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.monkeylearn.ExtraParam;
import com.monkeylearn.MonkeyLearn;
import com.monkeylearn.MonkeyLearnException;
import com.monkeylearn.MonkeyLearnResponse;

public class MonkeyLearnClient {

	private static final String API_KEY = "215b5633e6863bdf0a3f3fe6b8eec80d6752084b";
	
	// For testing purposes
//	public static void main(String[] args) {
//		String[] textList = {
//				"Elon Musk has shared a photo of the spacesuit designed by SpaceX. This is the second image shared of the new design and the first to feature the spacesuit’s full-body look.", };
//		List<List<String>> words = extractKeywords(textList);
//		for (List<String> ws : words) {
//			for (String w : ws) {
//				System.out.println(w);
//			}
//			System.out.println();
//		}
//	}

	public static List<List<String>> extractKeywords(String[] text) {
		if (text == null || text.length == 0) {
			return new ArrayList<>();
		}
		// Use the API key
		MonkeyLearn ml = new MonkeyLearn(API_KEY);
		// Use the keyword extractor
		ExtraParam[] extraParams = { new ExtraParam("max_keywords", "3") };
		MonkeyLearnResponse response;
		try {
			response = ml.extractors.extract("ex_YCya9nrn", text, extraParams);
			JSONArray resultArray = response.arrayResult;
			return getKeywords(resultArray);
		} catch (MonkeyLearnException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	/**
	 * Convert the result array's format ["front end developer", "black lives
	 * matter" "hello world"] ----> getKeywords <<"front", "end", "developer">,
	 * <"black", "lives", "matter">, <"hello", "world">>
	 */
	private static List<List<String>> getKeywords(JSONArray mlResultArray) {
		List<List<String>> topKeywords = new ArrayList<>();
		// Iterate the result array and convert it to our format.
		for (Object o : mlResultArray) {
			List<String> keywords = new ArrayList<>();
			JSONArray keywordsArray = (JSONArray) o; // needs to cast -> returns an object
//			System.out.println(keywordsArray);
			for (Object value : keywordsArray) {
				JSONObject keywordObject = (JSONObject) value;
				// We just need the keyword, excluding other fields.
				String keyword = (String) keywordObject.get("keyword");
				keywords.add(keyword);
			}
			topKeywords.add(keywords);
		}
		return topKeywords;
	}
}