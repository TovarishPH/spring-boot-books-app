package br.com.ph.learningspringbootapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.ph.learningspringbootapp.model.Image;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController("/api/images")
public class ImageController {

	@GetMapping
	public Flux<Image> images() {
		return Flux.just(
				new Image("1", "learning-spring-boot-cover.jpg"),
				new Image("2", "learning-spring-boot-2nd-edition-cover.jpg"),
				new Image("3", "bazinga.png")
				);
	}

	@PostMapping
	public Mono<Void> create(@RequestBody Flux<Image> images) {
		return images.map(image -> {
			System.out.println("We will save " + image + " to a Reactive database soon.");
			return image;
		}).then();

	}
}
