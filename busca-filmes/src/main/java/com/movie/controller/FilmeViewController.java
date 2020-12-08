package com.movie.controller;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xslf.usermodel.SlideLayout;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFSlideLayout;
import org.apache.poi.xslf.usermodel.XSLFSlideMaster;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.movie.model.MovieResponsethemoviedb;
import com.movie.model.RegistarBuscaIn;
import com.movie.service.SearchMovieServiceDB;

@Controller
@SessionAttributes("filmes")
public class FilmeViewController {

	@Autowired
	private SearchMovieServiceDB searchMovieService;	

	@GetMapping("/filme")
	public String filmeForm(Model model) {
		model.addAttribute("buscaIn", new RegistarBuscaIn());
		model.addAttribute("sem_filme", "");
		return "filme";
	}

	@PostMapping("/filme")
	public String registarBuscaFilme(@ModelAttribute RegistarBuscaIn buscaIn, Model model) {
		
		model.addAttribute("buscaIn", buscaIn);
		
	    buscaIn = (RegistarBuscaIn)model.getAttribute("buscaIn");	
	    
	    List<MovieResponsethemoviedb> filmes = searchMovieService.searchInfoMovieDB(buscaIn);
	    
	    model.addAttribute("filmes", filmes);
	    
	    if (filmes == null) {
	    	  model.addAttribute("sem_filme", "Sem filmes a exibir"); 	
	    } else {
	    	  model.addAttribute("sem_filme", "");
	    }
	    
	    return "filme";
	}
	
	@SuppressWarnings({ "resource", "unchecked" })
	@RequestMapping(value = "/downloadPPT", produces = "application/vnd.ms-powerpoint")
	public @ResponseBody byte[] downloadPPT(HttpServletResponse response, Model model) throws IOException {
		
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    byte[] bytes = null;
	   
	    
		XMLSlideShow ppt = new XMLSlideShow();
	    
	  
		List<MovieResponsethemoviedb> filmes = (List<MovieResponsethemoviedb>)model.getAttribute("filmes");
	    ppt.createSlide();
	    XSLFSlideMaster defaultMaster = ppt.getSlideMasters().get(0);

	    
	    filmes.forEach(filme-> {
	    	XSLFSlideLayout titleLayout = defaultMaster.getLayout(SlideLayout.TITLE);
		    XSLFSlide slide = ppt.createSlide(titleLayout);
	    	 XSLFTextShape body = slide.getPlaceholder(0);
	 	    
	 	    body.clearText();
	 	    body.addNewTextParagraph().addNewTextRun().setText("Nome do filme:".concat(filme.getOriginal_title()));
	 	    body.addNewTextParagraph().addNewTextRun().setText("Realizador(es):".concat(filme.getCriadores().toString()));
	 	    body.addNewTextParagraph().addNewTextRun().setText("Ano de lançamento:".concat(filme.getRelease_date()));
		    
	    });
	   
	   
	    // save changes in a file
	    FileOutputStream out = new FileOutputStream("slideshow.ppt");

	    ppt.write(outputStream);
	    out.close();
	    bytes = outputStream.toByteArray();

	    return bytes;

	}	
}
