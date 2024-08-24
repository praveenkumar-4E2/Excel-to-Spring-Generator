package com.application.generator.BaseFiles;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import com.application.generator.entity.TemplateRequest;

public class TemplateGenerator {

    public static String generateTemplate(TemplateRequest request) {
        // Base template with placeholders
        String classTemplate = "package {{packageName}};\n\n" +
                               "public class {{className}} {\n\n" +
                               "{{fields}}\n\n" +
                               "{{constructor}}\n\n" +
                               "{{gettersAndSetters}}\n" +
                               "}";

        StringBuilder fieldsBuilder = new StringBuilder();
        StringBuilder constructorParams = new StringBuilder();
        StringBuilder constructorBody = new StringBuilder();
        StringBuilder gettersAndSettersBuilder = new StringBuilder();

        // Loop through each column to build the class dynamically
        for (Map<String, String> column : request.getColumns()) {
            String type = column.get("type");
            String name = column.get("name");

            // Build the fields
            fieldsBuilder.append("    private ").append(type).append(" ").append(name).append(";\n");

            // Build the constructor parameters and body
            constructorParams.append(type).append(" ").append(name).append(", ");
            constructorBody.append("        this.").append(name).append(" = ").append(name).append(";\n");

            // Build getters and setters
            gettersAndSettersBuilder.append("    public ").append(type).append(" get")
                                    .append(capitalize(name)).append("() {\n")
                                    .append("        return ").append(name).append(";\n")
                                    .append("    }\n\n")
                                    .append("    public void set").append(capitalize(name)).append("(")
                                    .append(type).append(" ").append(name).append(") {\n")
                                    .append("        this.").append(name).append(" = ").append(name).append(";\n")
                                    .append("    }\n\n");
        }

        // Remove trailing comma from constructor parameters
        if (constructorParams.length() > 0) {
            constructorParams.setLength(constructorParams.length() - 2);
        }

        // Build the constructor with the dynamic content
        String constructor = "    public " + request.getClassName() + "(" + constructorParams.toString() + ") {\n" +
                             constructorBody.toString() + "    }\n";

        
        String result = classTemplate.replace("{{packageName}}", request.getPackageName())
                                     .replace("{{className}}", request.getClassName())
                                     .replace("{{fields}}", fieldsBuilder.toString())
                                     .replace("{{constructor}}", constructor)
                                     .replace("{{gettersAndSetters}}", gettersAndSettersBuilder.toString());

        return result;
    }
    
    public static String generateRepositoryTemplate(TemplateRequest request) {
        // Template for the repository interface
        String template = "package {{packageName}};\n\n" +
                          "import org.springframework.data.jpa.repository.JpaRepository;\n\n" +
                          "public interface {{className}}Repository extends JpaRepository<{{className}}, Long> {\n" +
                          "}";

        return template.replace("{{packageName}}", request.getPackageName() + ".repository")
                       .replace("{{className}}", request.getClassName());
    }
    
    
    public static String generateServiceTemplate(TemplateRequest request) {
        // Template for the service class
        String template = "package {{packageName}};\n\n" +
                          "import java.util.List;\n" +
                          "import org.springframework.beans.factory.annotation.Autowired;\n" +
                          "import org.springframework.stereotype.Service;\n\n" +
                          "@Service\n" +
                          "public class {{className}}Service {\n\n" +
                          "    @Autowired\n" +
                          "    private {{className}}Repository repository;\n\n" +
                          "    public List<{{className}}> findAll() {\n" +
                          "        return repository.findAll();\n" +
                          "    }\n\n" +
                          "    public {{className}} save({{className}} entity) {\n" +
                          "        return repository.save(entity);\n" +
                          "    }\n\n" +
                          "    public void deleteById(Long id) {\n" +
                          "        repository.deleteById(id);\n" +
                          "    }\n\n" +
                          "    public {{className}} findById(Long id) {\n" +
                          "        return repository.findById(id).orElse(null);\n" +
                          "    }\n" +
                          "}";

        return template.replace("{{packageName}}", request.getPackageName() + ".service")
                       .replace("{{className}}", request.getClassName());
    }


    public static String writeToFile(String content, String fileName) throws IOException {
        // Specify the directory where you want to store the file
        String directory = "E:/DeployIngCode/Excel-to-Spring-Generator/GeneratedProject/";
        
        // Create the directory if it doesn't exist
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // Define the file path and name
        String filePath = directory + fileName;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
        }
        return filePath;
    }

    private static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
