package com.johansvartdal.SpringAI.authentication;

import com.johansvartdal.SpringAI.annotation.NoCors;
import com.johansvartdal.SpringAI.annotation.NoLogin;
import com.johansvartdal.SpringAI.annotation.NoSecurity;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

@Slf4j
@Getter
@Component
public class NoSecurityProcessor {

    private final ArrayList<String> noLoginPaths = new ArrayList<>();
    private final ArrayList<String> allowAllOriginsPaths = new ArrayList<>();

    public void findNoSecurityPaths() {
        log.debug("Starting NoSecurityProcessor...");

        // Use Reflections library to get all methods in the package with specific annotations
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages("com.johansvartdal.SpringAI.controller")
                .addScanners(new MethodAnnotationsScanner()));

        // Get all methods annotated with NoSecurity
        Set<Method> methods = reflections.getMethodsAnnotatedWith(NoSecurity.class);
        methods.addAll(reflections.getMethodsAnnotatedWith(NoLogin.class));
        methods.addAll(reflections.getMethodsAnnotatedWith(NoCors.class));

        log.debug("NoSecurity processor found {} annotated methods", methods.size());

        for (Method method : methods) {
            if (!method.getDeclaringClass().isAnnotationPresent(RequestMapping.class)) {
                log.warn("Found a NoSecurity annotation outside a class with a RequestMapping annotation");
                continue;
            }

            String beginPath = extractPathFromRequestMapping(method.getDeclaringClass().getAnnotation(RequestMapping.class));
            String endPath = extractPathFromMethod(method);

            if (beginPath == null || endPath == null) {
                continue;
            }

            String fullPath = beginPath + endPath;
            if (method.isAnnotationPresent(NoCors.class)) {
                allowAllOriginsPaths.add(fullPath);
                log.debug("Exposed path for NoSecurity [NoCors]: " + fullPath);
            }else if (method.isAnnotationPresent(NoLogin.class)) {
                noLoginPaths.add(fullPath);
                log.debug("Exposed path for NoSecurity [NoLogin]: " + fullPath);
            }else if (method.isAnnotationPresent(NoSecurity.class)) {
                noLoginPaths.add(fullPath);
                allowAllOriginsPaths.add(fullPath);
                log.debug("Exposed path for NoSecurity [all]: {}", fullPath);
            }
        }
    }

    private String extractPathFromRequestMapping(RequestMapping requestMapping) {
        if (requestMapping != null) {
            if (requestMapping.value().length > 0) {
                return requestMapping.value()[0];
            } else if (requestMapping.path().length > 0) {
                return requestMapping.path()[0];
            }
        }
        return null;
    }

    private String extractPathFromMethod(Method method) {
        if (method.isAnnotationPresent(PostMapping.class)) {
            return extractPathFromMapping(method.getAnnotation(PostMapping.class));
        } else if (method.isAnnotationPresent(GetMapping.class)) {
            return extractPathFromMapping(method.getAnnotation(GetMapping.class));
        } else if (method.isAnnotationPresent(PutMapping.class)) {
            return extractPathFromMapping(method.getAnnotation(PutMapping.class));
        } else if (method.isAnnotationPresent(DeleteMapping.class)) {
            return extractPathFromMapping(method.getAnnotation(DeleteMapping.class));
        }
        return null;
    }

    private String extractPathFromMapping(Object mapping) {
        if (mapping != null) {
            try {
                Method valueMethod = mapping.getClass().getMethod("value");
                Method pathMethod = mapping.getClass().getMethod("path");
                String[] value = (String[]) valueMethod.invoke(mapping);
                String[] path = (String[]) pathMethod.invoke(mapping);
                if (value.length > 0) {
                    return value[0];
                } else if (path.length > 0) {
                    return path[0];
                }
            } catch (Exception e) {
                log.error("Error extracting path from mapping annotation", e);
            }
        }
        return null;
    }
}
