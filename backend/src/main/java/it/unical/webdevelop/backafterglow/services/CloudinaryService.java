package it.unical.webdevelop.backafterglow.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    //Caricamento di un'immagine in una cartella precisa
    public String uploadImage(MultipartFile file, String folder) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "afterglow/" + folder,  // Cartella su Cloudinary
                        "resource_type", "auto"
                ));

        String url = (String) uploadResult.get("secure_url");
        System.out.println("📸 Foto caricata su Cloudinary: " + url);
        return url;
    }


    public void deleteImage(String publicId) throws IOException {
        Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        System.out.println("🗑️ Foto eliminata da Cloudinary: " + result);
    }


    public String extractPublicId(String url) {
        if (url == null || !url.contains("cloudinary.com")) {
            return null;
        }


        String[] parts = url.split("/upload/");
        if (parts.length < 2) return null;

        String pathWithVersion = parts[1];
        String path = pathWithVersion.replaceFirst("v\\d+/", "");

        return path.substring(0, path.lastIndexOf('.'));
    }
}
