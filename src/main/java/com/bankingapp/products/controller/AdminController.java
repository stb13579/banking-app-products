package com.bankingapp.products.controller;

import com.bankingapp.products.entity.Product;
import com.bankingapp.products.repository.ProductRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    @Autowired
    private ProductRepository productRepository;

    @PostMapping(value = "/products/import", consumes = MediaType.APPLICATION_XML_VALUE)
    @Operation(summary = "Import products from XML")
    public ResponseEntity<Map<String, Object>> importProducts(@RequestBody String xmlPayload)
            throws Exception {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xmlPayload)));

        NodeList nodes = doc.getElementsByTagName("product");
        List<Product> imported = new ArrayList<>();

        for (int i = 0; i < nodes.getLength(); i++) {
            Element el = (Element) nodes.item(i);
            Product p = new Product();
            p.setName(text(el, "name"));
            p.setType(text(el, "type"));
            p.setDescription(text(el, "description"));
            String aprStr = text(el, "apr");
            if (aprStr != null && !aprStr.isBlank()) {
                p.setApr(new BigDecimal(aprStr.trim()));
            }
            p.setStatus("ACTIVE");
            imported.add(productRepository.save(p));
        }

        return ResponseEntity.ok(Map.of("imported", imported.size(), "products", imported));
    }

    private String text(Element parent, String tagName) {
        NodeList list = parent.getElementsByTagName(tagName);
        if (list.getLength() == 0) return null;
        return list.item(0).getTextContent();
    }
}
