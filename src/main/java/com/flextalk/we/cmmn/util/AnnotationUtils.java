package com.flextalk.we.cmmn.util;

import com.flextalk.we.cmmn.auth.ApiPermission;

import java.util.Objects;

public final class AnnotationUtils {

    public static ApiPermission getPermissionPriority(ApiPermission classAnnotation, ApiPermission methodAnnotation) {

        if(Objects.isNull(classAnnotation) && Objects.isNull(methodAnnotation)) {
            throw new IllegalStateException();
        }

        if(Objects.isNull(methodAnnotation)) {
            return classAnnotation;
        }

        return methodAnnotation;
    }
}
