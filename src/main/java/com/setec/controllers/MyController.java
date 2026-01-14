package com.setec.controllers;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.setec.entities.PostProductDAO;
import com.setec.entities.Product;
import com.setec.entities.PutProductDAO;
import com.setec.repos.ProductRepo;

@RestController
@RequestMapping("/api/product")
public class MyController {
	
	//http://localhost:8080/swagger-ui/index.html
	
	@Autowired
	private ProductRepo productRepo;
	
	@GetMapping
	public Object getAll() {
		var products = productRepo.findAll();
		if(products.isEmpty()) {
			return ResponseEntity.status(404).
					body(Map.of("message", "Product is empty"));
		}
		return productRepo.findAll();
	}
	
//for Local Deploy
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
	public ResponseEntity<?> addProduct(@ModelAttribute PostProductDAO postProductDAO)
			throws Exception {
		String uploadDir = new File("MyApp/static").getAbsolutePath();
		File dir = new File(uploadDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		var file = postProductDAO.getFile();
		String uniqueName = UUID.randomUUID()+"_"+file.getOriginalFilename();
		String filePath = Paths.get(uploadDir, uniqueName).toString();
		
		file.transferTo(new File(filePath));
		
		var pro = new Product();
		pro.setName(postProductDAO.getName());
		pro.setPrice(postProductDAO.getPrice());
		pro.setQty(postProductDAO.getQty());
		pro.setImageUrl("/static/" + uniqueName);
		
		productRepo.save(pro);
		
		return ResponseEntity.status(201).body(pro);
	}

//from testing
//	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//	public ResponseEntity<?> addProduct(@ModelAttribute PostProductDAO postProductDAO) throws Exception {
//
//	    // Use a folder inside the container
//	    String uploadDir = "/app/uploads";
//	    File dir = new File(uploadDir);
//	    if (!dir.exists()) {
//	        dir.mkdirs();
//	    }
//
//	    var file = postProductDAO.getFile();
//	    String uniqueName = UUID.randomUUID() + "_" + file.getOriginalFilename();
//	    Path filePath = Paths.get(uploadDir, uniqueName);
//	    
//	    // Save the file
//	    file.transferTo(filePath.toFile());
//
//	    var pro = new Product();
//	    pro.setName(postProductDAO.getName());
//	    pro.setPrice(postProductDAO.getPrice());
//	    pro.setQty(postProductDAO.getQty());
//	    
//	    // URL served via Spring mapping
//	    pro.setImageUrl("/static/" + uniqueName);
//
//	    productRepo.save(pro);
//
//	    return ResponseEntity.status(201).body(pro);
//	}
	
// for Local Deploy
	@PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
	public ResponseEntity<?> updateProduct(@ModelAttribute PutProductDAO putProductDAO)
			throws Exception {
		var p = productRepo.findById(putProductDAO.getId());
		if (p.isPresent()) {
			var update = p.get();
			update.setName(putProductDAO.getName());
			update.setPrice(putProductDAO.getPrice());
			update.setQty(putProductDAO.getQty());
			if(putProductDAO.getFile()!=null) {
				String uploadDir = new File("MyApp/static").getAbsolutePath(); 
				File dir = new File(uploadDir);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				var file = putProductDAO.getFile();
				String uniqueName = UUID.randomUUID()+"_"+file.getOriginalFilename();
				String filePath = Paths.get(uploadDir, uniqueName).toString();
				
				new File("MyApp/" + update.getImageUrl()).delete();
				
				file.transferTo(new File(filePath));
				update.setImageUrl("/static/" + uniqueName);
			}
			productRepo.save(update);
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(p.get());
		}
		
		return ResponseEntity.status(404).body(Map.of("message", 
				"Product id = " +putProductDAO.getId() + " not found"));
	}

//for testing
//	@PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//	public ResponseEntity<?> updateProduct(@ModelAttribute PutProductDAO putProductDAO) throws Exception {
//
//	    var p = productRepo.findById(putProductDAO.getId());
//	    if (p.isPresent()) {
//	        var update = p.get();
//	        update.setName(putProductDAO.getName());
//	        update.setPrice(putProductDAO.getPrice());
//	        update.setQty(putProductDAO.getQty());
//
//	        var file = putProductDAO.getFile();
//	        if (file != null && !file.isEmpty()) {
//
//	            // Use the same temporary folder as addProduct
//	            String uploadDir = "/app/uploads";
//	            File dir = new File(uploadDir);
//	            if (!dir.exists()) {
//	                dir.mkdirs();
//	            }
//
//	            // Delete old file if exists
//	            if (update.getImageUrl() != null && !update.getImageUrl().isBlank()) {
//	                String oldFileName = update.getImageUrl().replace("/static/", "");
//	                File oldFile = new File(uploadDir, oldFileName);
//	                if (oldFile.exists()) {
//	                    oldFile.delete();
//	                }
//	            }
//
//	            // Save new file
//	            String uniqueName = UUID.randomUUID() + "_" + file.getOriginalFilename();
//	            Path filePath = Paths.get(uploadDir, uniqueName);
//	            file.transferTo(filePath.toFile());
//
//	            // Update image URL
//	            update.setImageUrl("/static/" + uniqueName);
//	        }
//
//	        productRepo.save(update);
//	        return ResponseEntity.status(HttpStatus.ACCEPTED).body(update);
//	    }
//
//	    return ResponseEntity.status(404).body(Map.of(
//	            "message", "Product id = " + putProductDAO.getId() + " not found"
//	    ));
//	}
	
	@GetMapping({"/{id}","/id/{id}"})
	public ResponseEntity<?> getById(@PathVariable("id") Integer id){
		var pro = productRepo.findById(id);
		if(pro.isPresent()) {
			return ResponseEntity.status(200).body(pro.get());
		}
		return ResponseEntity.status(404).body(Map.of("message", "Product id = " + id + " is not found"));
	}
	
// for Local Deploy
	@DeleteMapping({"/{id}","/id/{id}"})
	public ResponseEntity<?> deleteById(@PathVariable("id") Integer id){
		var pro = productRepo.findById(id);
		if(pro.isPresent()) {
			new File("MyApp/"+pro.get().getImageUrl()).delete();
			productRepo.delete(pro.get());
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body(Map.of("message", "Product id = " + id + " has been deleted"));
		}
		return ResponseEntity.status(404).body(Map.of("message", "Product id = " + id + " is not found"));
	}

//for testing
//	@DeleteMapping({"/{id}", "/id/{id}"})
//	public ResponseEntity<?> deleteById(@PathVariable("id") Integer id) {
//	    var pro = productRepo.findById(id);
//	    if (pro.isPresent()) {
//	        var product = pro.get();
//
//	        // Delete the image from /app/uploads if it exists
//	        if (product.getImageUrl() != null && !product.getImageUrl().isBlank()) {
//	            String uploadDir = "/app/uploads";
//	            String fileName = product.getImageUrl().replace("/static/", "");
//	            File file = new File(uploadDir, fileName);
//	            if (file.exists()) {
//	                file.delete();
//	            }
//	        }
//
//	        // Delete the product from the database
//	        productRepo.delete(product);
//
//	        return ResponseEntity.status(HttpStatus.ACCEPTED)
//	                .body(Map.of("message", "Product id = " + id + " has been deleted"));
//	    }
//
//	    return ResponseEntity.status(404)
//	            .body(Map.of("message", "Product id = " + id + " is not found"));
//	}
}
