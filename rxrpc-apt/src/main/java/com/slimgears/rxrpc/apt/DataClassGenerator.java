/**
 *
 */
package com.slimgears.rxrpc.apt;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.slimgears.rxrpc.apt.data.PropertyInfo;
import com.slimgears.rxrpc.apt.internal.CodeGenerator;

public interface DataClassGenerator extends CodeGenerator<DataClassGenerator.Context> {
    @AutoValue
    abstract class Context extends CodeGenerator.Context {
        public abstract ImmutableList<PropertyInfo> properties();
        public abstract ImmutableList<PropertyInfo> inheritedProperties();

        public static Builder builder() {
            return new AutoValue_DataClassGenerator_Context.Builder();
        }

        @AutoValue.Builder
        public interface Builder extends CodeGenerator.Context.Builder<Context, Builder> {
            ImmutableList.Builder<PropertyInfo> propertiesBuilder();
            ImmutableList.Builder<PropertyInfo> inheritedPropertiesBuilder();

            default Builder property(PropertyInfo property) {
                propertiesBuilder().add(property);
                return this;
            }

            default Builder inheritedProperty(PropertyInfo propertyInfo) {
                inheritedPropertiesBuilder().add(propertyInfo);
                return this;
            }
        }
    }
}
