package com.movie.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.movie.model.CriadorMoviedb;
import com.movie.model.ElencoMovieDB;
import com.movie.model.MovieResponsethemoviedb;
import com.movie.model.RegistarBuscaIn;

@Service
public class SearchMovieServiceDB {
	
	 private static Log logger = LogFactory.getLog(SearchMovieServiceDB.class);

	@Autowired
	public RestTemplate restTemplate;
	
	@Autowired
	public SearchMovieServiceOMDB searchMovieServiceOMDB;

	@Value("${url.search.actor}")
	private String urlSearchActor;
	
	@Value("${url.search.movie.actor}")
	private String urlSearchMovieActor;
	
	@Value("${url.search.movie.creators}")
    private String urlCreatorsMovieMovieDB;
	
	@Value("${url.search.movie.elenco}")
	private String urlSearchElenco;

	public List<MovieResponsethemoviedb> searchInfoMovieDB(RegistarBuscaIn in) {
		
		try {
			
			Integer atorId = getInfoAtorId(in);
			
			if (atorId == null) {
				return null;
			}
			return searchMovies(atorId);
			
		} catch (Exception e) {
			logger.error("Erro ao obter a lista de filmes: {}", e);
			return null;
		}

	}

	private Integer getInfoAtorId(RegistarBuscaIn info) {
		String response = restTemplate.getForObject(urlSearchActor.concat(info.getAtor()), String.class);

		JSONObject jsonObject = new JSONObject(response);

		if (Integer.valueOf(jsonObject.get("total_pages").toString()) == 0) {
			return null;
		}

		return (Integer) jsonObject.getJSONArray("results").getJSONObject(0).get("id");
	}
	
	public List<MovieResponsethemoviedb> searchMovies(Integer id) {
		
		String responseInfoMovies = restTemplate.getForObject(urlSearchMovieActor.concat(Integer.toString(id)), String.class);
		JSONObject jsonObject = new JSONObject(responseInfoMovies);
		JSONArray matrixMovies = jsonObject.getJSONArray("results");
		
		Gson gson = null;
		
	    List<MovieResponsethemoviedb> movies = new ArrayList<MovieResponsethemoviedb>();
		
		for (int i = 0; i < matrixMovies.length(); i++) {
			
			gson = new Gson();
			MovieResponsethemoviedb movieDB = gson.fromJson(matrixMovies.get(i).toString(), MovieResponsethemoviedb.class);
			MovieResponsethemoviedb movieOMBD = searchMovieServiceOMDB.searchMovies(movieDB.getOriginal_title());
			
			String responseCreatorsMovies = restTemplate.getForObject(urlCreatorsMovieMovieDB.replace("{movie_id}", id.toString()),String.class);
			jsonObject = new JSONObject(responseCreatorsMovies);			
			
			JSONArray matrizCreators = jsonObject.getJSONArray("production_companies");
			StringBuilder criadores = new StringBuilder();
			
			for (int j = 0 ; j < matrizCreators.length(); j++) {
				
				gson = new Gson();
				CriadorMoviedb criadorMovie = gson.fromJson(matrizCreators.get(j).toString(), CriadorMoviedb.class);
				criadores.append(criadorMovie.getName().concat(" "));				
				
			}	
			
			String searchCast = restTemplate.getForObject(urlSearchElenco.replace("{movie_id}", id.toString()), String.class);
			JSONObject jsonElenco = new JSONObject(searchCast);
			JSONArray matrizElenco = jsonElenco.getJSONArray("cast");
			
			StringBuilder elencoFilme = new StringBuilder();
		
			for (int z = 0; z < matrizElenco.length(); z++) {
				gson = new Gson();
				ElencoMovieDB elencoMovieDb = gson.fromJson(matrizElenco.get(z).toString(), ElencoMovieDB.class);
				elencoFilme.append(elencoMovieDb.getName().concat(","));
			}
			
			movieDB.setCriadores(criadores);		
			movieDB.setElenco(elencoFilme);
			
			movies.add(movieOMBD);
			movies.add(movieDB);
			
		}		
		
		return movies;		
	}
}
