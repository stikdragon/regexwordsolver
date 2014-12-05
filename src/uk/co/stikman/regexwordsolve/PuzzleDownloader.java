package uk.co.stikman.regexwordsolve;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PuzzleDownloader {
	public List<Puzzle> go() {
		try {
			URL website = new URL("http://regexcrossword.com/data/challenges.json");
			InputStream is = website.openStream();
			InputStreamReader isr = new InputStreamReader(is);
			String json;
			try (Scanner s = new Scanner(is, "UTF-8")) {
				s.useDelimiter("\\A");
				json = s.hasNext() ? s.next() : "";
			}
			JSONArray arr = new JSONArray(json);
			List<Puzzle> result = new ArrayList<>();
			for (int i = 0; i < arr.length(); ++i) {
				JSONObject jsGrp = arr.getJSONObject(i);
				String grpName = jsGrp.getString("name");

				JSONArray arr2 = jsGrp.getJSONArray("puzzles");
				for (int j = 0; j < arr2.length(); ++j) {
					try {
						JSONObject jsPuzz = arr2.getJSONObject(j);
						Puzzle p = new Puzzle();
						p.setGroup(grpName);
						p.setName(jsPuzz.getString("name"));
						JSONArray arr3 = jsPuzz.getJSONArray("patternsY");
						for (int k = 0; k < arr3.length(); ++k) {
							JSONArray arr4 = arr3.getJSONArray(k);
							for (int z = 0; z < arr4.length(); ++z) {
								if (z >= 2)
									throw new JSONException("More than two expressions in X");
								p.setPattern(z == 0 ? Side.W : Side.E, k, arr4.getString(z));
							}
						}

						arr3 = jsPuzz.getJSONArray("patternsX");
						for (int k = 0; k < arr3.length(); ++k) {
							JSONArray arr4 = arr3.getJSONArray(k);
							for (int z = 0; z < arr4.length(); ++z) {
								if (z >= 2)
									throw new JSONException("More than two expressions in Y");
								p.setPattern(z == 0 ? Side.N : Side.S, k, arr4.getString(z));
							}
						}

						result.add(p);
					} catch (Throwable th) {
						th.printStackTrace();
					}
				}
			}

			return result;

		} catch (IOException | JSONException e) {
			throw new RuntimeException("Couldn't download source", e);
		}
	}
}
