package al.aldi.annotation3.processor;

import al.aldi.annotation3.annotation.Builder;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;
import com.google.auto.service.AutoService;

@AutoService(Processor.class)
@SupportedAnnotationTypes("al.aldi.annotation3.annotation.Builder")
@SupportedSourceVersion(SourceVersion.RELEASE_23)
public class BuilderProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(Builder.class)) {
            if (annotatedElement.getKind() != ElementKind.CLASS) {
                return true; // Skip non-class elements
            }

            TypeElement typeElement = (TypeElement) annotatedElement;
            String className = typeElement.getSimpleName().toString();
            String packageName = processingEnv.getElementUtils().getPackageOf(typeElement).toString();
            String builderClassName = className + "Builder";

            try {
                generateBuilderClass(typeElement, className, builderClassName, packageName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private void generateBuilderClass(TypeElement typeElement, String className, String builderClassName, String packageName) throws IOException {
        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(packageName + "." + builderClassName);

        try (Writer writer = builderFile.openWriter()) {
            writer.write("package " + packageName + ";\n\n");
            writer.write("public class " + builderClassName + " {\n");

            typeElement.getEnclosedElements().stream()
                    .filter(e -> e.getKind() == ElementKind.FIELD)
                    .forEach(field -> {
                        String fieldName = field.getSimpleName().toString();
                        String fieldType = field.asType().toString();
                        try {
                            writer.write("    private " + fieldType + " " + fieldName + ";\n");
                            writer.write("    public " + builderClassName + " set" + capitalize(fieldName) + "(" + fieldType + " " + fieldName + ") {\n");
                            writer.write("        this." + fieldName + " = " + fieldName + ";\n");
                            writer.write("        return this;\n");
                            writer.write("    }\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

            writer.write("    public " + className + " build() {\n");
            writer.write("        " + className + " instance = new " + className + "();\n");

            typeElement.getEnclosedElements().stream()
                    .filter(e -> e.getKind() == ElementKind.FIELD)
                    .forEach(field -> {
                        String fieldName = field.getSimpleName().toString();
                        try {
                            writer.write("        instance." + fieldName + " = this." + fieldName + ";\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

            writer.write("        return instance;\n");
            writer.write("    }\n");
            writer.write("}\n");
        }
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
