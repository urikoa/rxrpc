/**
 *
 */
package com.slimgears.rxrpc.apt.data;

import com.google.common.collect.ImmutableList;

import javax.lang.model.element.ExecutableElement;

public interface HasMethods {
    ImmutableList<MethodInfo> methods();

    interface Builder<B extends Builder<B>> {
        ImmutableList.Builder<MethodInfo> methodsBuilder();

        default B method(ExecutableElement element) {
            return method(MethodInfo.of(element));
        }

        default B method(MethodInfo method) {
            methodsBuilder().add(method);
            //noinspection unchecked
            return (B)this;
        }
    }
}