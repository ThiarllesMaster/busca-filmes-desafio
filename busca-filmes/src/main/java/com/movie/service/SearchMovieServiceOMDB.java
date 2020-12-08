package com.movie.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.movie.model.MovieResponsethemoviedb;

@Service
public class SearchMovieServiceOMDB {

	@Value("${url.search.omdb.movie}")
	private String urlSearchMoviesOMDB;

	@Autowired
	private RestTemplate restTemplate;

	public MovieResponsethemoviedb searchMovies(String movieName) {

		String responseInfoMovies = restTemplate.getForObject(urlSearchMoviesOMDB.replace("{movie}", movieName),
				String.class);
		JSONObject jsonObject = new JSONObject(responseInfoMovies);

		MovieResponsethemoviedb movie = new MovieResponsethemoviedb();
		if (jsonObject.has("Error")) {
			movie.setOriginal_title("Não encontrado");
			movie.setRelease_date("Não encontrado");
			movie.setElenco(new StringBuilder("Não encontrado"));
			movie.setCriadores(new StringBuilder("Não encontrado"));

		} else {
			movie.setOriginal_title(jsonObject.getString("Title"));
			movie.setRelease_date(jsonObject.getString("Released"));
			movie.setElenco(new StringBuilder(jsonObject.get("Actors").toString()));
			movie.setCriadores(new StringBuilder(jsonObject.get("Production").toString()));
		}
		return movie;

	}
}
