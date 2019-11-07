package br.com.ph.learningspringbootapp.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;

import br.com.ph.learningspringbootapp.model.Image;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ImageService {

	private static String UPLOAD_ROOT = "/home/paulo/Documentos/teste";

	private final ResourceLoader resourceLoader;

	public ImageService(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	/**
	 * * Pre-load some test images
	 *
	 * @return Spring Boot {@link CommandLineRunner} automatically run after app
	 *         context is loaded.
	 * @throws IOException
	 */
	@Bean
	public CommandLineRunner setUp() throws IOException {
		return (args) -> {
			FileSystemUtils.deleteRecursively(new File(UPLOAD_ROOT));

			Files.createDirectory(Paths.get(UPLOAD_ROOT));

			FileCopyUtils.copy("Teste", new FileWriter(UPLOAD_ROOT + "/knitler"));
			FileCopyUtils.copy("Teste2", new FileWriter(UPLOAD_ROOT + "/grumpy-cat"));
			FileCopyUtils.copy("Teste3", new FileWriter(UPLOAD_ROOT + "/cold-brew"));

		};
	}

	public Flux<Image> findAllImages() {
		try { 
			return Flux.fromIterable(Files.newDirectoryStream(Paths.get(UPLOAD_ROOT)))
					.map(path -> new Image(null, path.getFileName().toString()));
		} catch (IOException e) {
			return Flux.empty();
		}
		
	}
	
	public Mono<Resource> findOneImage(String fileName) {
		return Mono.fromSupplier(() -> resourceLoader.getResource("file: " + UPLOAD_ROOT + "/" + fileName));
	}
	
	public Mono<Void> createImage(Flux<FilePart> files) {
		return files.flatMap(file -> file.transferTo(Paths.get(UPLOAD_ROOT, file.filename()).toFile())).then();
	}
	
	public Mono<Void> deleteImage(String fileName) {
		return Mono.fromRunnable(() -> {
			try {
				Files.deleteIfExists(Paths.get(UPLOAD_ROOT, fileName));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}
}
