/*
 * The MIT License
 *
 * Copyright (c) 2018 ActiveEngine.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ae.db.processor;

import ae.db.*;
import com.google.common.collect.ImmutableMap;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.LinkedList;

final class FieldGenerator extends AttributeGenerator {

        private static final ImmutableMap<TypeName, ClassName> INDEXED_BY_TYPE_NAME;

        static {
                INDEXED_BY_TYPE_NAME = ImmutableMap.<TypeName, ClassName>builder()
                        .put(FieldType.BLOB_KEY, ClassName.get(IndexedBlobKey.class))
                        .put(FieldType.BOOLEAN, ClassName.get(IndexedBoolean.class))
                        .put(FieldType.CATEGORY, ClassName.get(IndexedCategory.class))
                        .put(FieldType.DATE, ClassName.get(IndexedDate.class))
                        .put(FieldType.DOUBLE, ClassName.get(IndexedDouble.class))
                        .put(FieldType.EMAIL, ClassName.get(IndexedEmail.class))
                        .put(FieldType.GEO_PT, ClassName.get(IndexedGeoPt.class))
                        .put(FieldType.IM_HANDLE, ClassName.get(IndexedIMHandle.class))
                        .put(FieldType.KEY, ClassName.get(IndexedKey.class))
                        .put(FieldType.LINK, ClassName.get(IndexedLink.class))
                        .put(FieldType.LONG, ClassName.get(IndexedLong.class))
                        .put(FieldType.PHONE_NUMBER, ClassName.get(IndexedPhoneNumber.class))
                        .put(FieldType.POSTAL_ADDRESS, ClassName.get(IndexedPostalAddress.class))
                        .put(FieldType.RATING, ClassName.get(IndexedRating.class))
                        .put(FieldType.SHORT_BLOB, ClassName.get(IndexedShortBlob.class))
                        .put(FieldType.STRING, ClassName.get(IndexedString.class))
                        .put(FieldType.USER, ClassName.get(IndexedUser.class))
                        .put(FieldType.BLOB_KEY_LIST, ClassName.get(IndexedBlobKeyList.class))
                        .put(FieldType.BOOLEAN_LIST, ClassName.get(IndexedBooleanList.class))
                        .put(FieldType.CATEGORY_LIST, ClassName.get(IndexedCategoryList.class))
                        .put(FieldType.DATE_LIST, ClassName.get(IndexedDateList.class))
                        .put(FieldType.DOUBLE_LIST, ClassName.get(IndexedDoubleList.class))
                        .put(FieldType.EMAIL_LIST, ClassName.get(IndexedEmailList.class))
                        .put(FieldType.GEO_PT_LIST, ClassName.get(IndexedGeoPtList.class))
                        .put(FieldType.IM_HANDLE_LIST, ClassName.get(IndexedIMHandleList.class))
                        .put(FieldType.KEY_LIST, ClassName.get(IndexedKeyList.class))
                        .put(FieldType.LINK_LIST, ClassName.get(IndexedLinkList.class))
                        .put(FieldType.LONG_LIST, ClassName.get(IndexedLongList.class))
                        .put(FieldType.PHONE_NUMBER_LIST, ClassName.get(IndexedPhoneNumberList.class))
                        .put(FieldType.POSTAL_ADDRESS_LIST, ClassName.get(IndexedPostalAddressList.class))
                        .put(FieldType.RATING_LIST, ClassName.get(IndexedRatingList.class))
                        .put(FieldType.SHORT_BLOB_LIST, ClassName.get(IndexedShortBlobList.class))
                        .put(FieldType.STRING_LIST, ClassName.get(IndexedStringList.class))
                        .put(FieldType.USER_LIST, ClassName.get(IndexedUserList.class))
                        .build();
        }

        private static final ImmutableMap<TypeName, ClassName> UNINDEXED_BY_TYPE_NAME;

        static {
                UNINDEXED_BY_TYPE_NAME = ImmutableMap.<TypeName, ClassName>builder()
                        .put(FieldType.BLOB, ClassName.get(UnindexedBlob.class))
                        .put(FieldType.BLOB_KEY, ClassName.get(IndexedBlobKey.class))
                        .put(FieldType.BOOLEAN, ClassName.get(IndexedBoolean.class))
                        .put(FieldType.CATEGORY, ClassName.get(IndexedCategory.class))
                        .put(FieldType.DATE, ClassName.get(IndexedDate.class))
                        .put(FieldType.DOUBLE, ClassName.get(IndexedDouble.class))
                        .put(FieldType.EMAIL, ClassName.get(IndexedEmail.class))
                        .put(FieldType.EMBEDDED_ENTITY, ClassName.get(UnindexedEmbeddedEntity.class))
                        .put(FieldType.GEO_PT, ClassName.get(IndexedGeoPt.class))
                        .put(FieldType.IM_HANDLE, ClassName.get(IndexedIMHandle.class))
                        .put(FieldType.KEY, ClassName.get(IndexedKey.class))
                        .put(FieldType.LINK, ClassName.get(IndexedLink.class))
                        .put(FieldType.LONG, ClassName.get(IndexedLong.class))
                        .put(FieldType.PHONE_NUMBER, ClassName.get(IndexedPhoneNumber.class))
                        .put(FieldType.POSTAL_ADDRESS, ClassName.get(IndexedPostalAddress.class))
                        .put(FieldType.RATING, ClassName.get(IndexedRating.class))
                        .put(FieldType.SHORT_BLOB, ClassName.get(IndexedShortBlob.class))
                        .put(FieldType.STRING, ClassName.get(IndexedString.class))
                        .put(FieldType.TEXT, ClassName.get(UnindexedText.class))
                        .put(FieldType.USER, ClassName.get(IndexedUser.class))
                        .put(FieldType.BLOB_LIST, ClassName.get(UnindexedBlobList.class))
                        .put(FieldType.BLOB_KEY_LIST, ClassName.get(IndexedBlobKeyList.class))
                        .put(FieldType.BOOLEAN_LIST, ClassName.get(IndexedBooleanList.class))
                        .put(FieldType.CATEGORY_LIST, ClassName.get(IndexedCategoryList.class))
                        .put(FieldType.DATE_LIST, ClassName.get(IndexedDateList.class))
                        .put(FieldType.DOUBLE_LIST, ClassName.get(IndexedDoubleList.class))
                        .put(FieldType.EMAIL_LIST, ClassName.get(IndexedEmailList.class))
                        .put(FieldType.EMBEDDED_ENTITY_LIST, ClassName.get(UnindexedEmbeddedEntityList.class))
                        .put(FieldType.GEO_PT_LIST, ClassName.get(IndexedGeoPtList.class))
                        .put(FieldType.IM_HANDLE_LIST, ClassName.get(IndexedIMHandleList.class))
                        .put(FieldType.KEY_LIST, ClassName.get(IndexedKeyList.class))
                        .put(FieldType.LINK_LIST, ClassName.get(IndexedLinkList.class))
                        .put(FieldType.LONG_LIST, ClassName.get(IndexedLongList.class))
                        .put(FieldType.PHONE_NUMBER_LIST, ClassName.get(IndexedPhoneNumberList.class))
                        .put(FieldType.POSTAL_ADDRESS_LIST, ClassName.get(IndexedPostalAddressList.class))
                        .put(FieldType.RATING_LIST, ClassName.get(IndexedRatingList.class))
                        .put(FieldType.SHORT_BLOB_LIST, ClassName.get(IndexedShortBlobList.class))
                        .put(FieldType.STRING_LIST, ClassName.get(IndexedStringList.class))
                        .put(FieldType.TEXT_LIST, ClassName.get(UnindexedTextList.class))
                        .put(FieldType.USER_LIST, ClassName.get(IndexedUserList.class))
                        .build();
        }

        private final TypeName fieldClass;
        final MetaField field;

        FieldGenerator(final MetaField field, final ClassName modelClass)
        {
                super(modelClass);
                this.fieldClass = mappedFieldClass(field);
                this.field = field;
        }

        @Override
        final String canonicalName()
        {
                return field.canonicalNameAt(modelClass.toString());
        }

        @Override
        void buildAt(final TypeSpec.Builder modelSpec)
        {
                modelSpec.addField(
                        FieldSpec.builder(fieldClass, field.name, attributeModifiers(field))
                                .initializer(fieldInitializerFormat(), fieldInitializerArgs())
                                .build()
                );
        }

        protected String fieldInitializerFormat()
        {
                final String constraints = getConstraints(field);
                if (isDate()) {
                        return "new $T(canonicalName($S), description($S), propertyName($S), fieldName($S), $L, jsonName($S), jsonPath($S), new $T($S), " + constraints + ')';
                } else {
                        return "new $T(canonicalName($S), description($S), propertyName($S), fieldName($S), $L, jsonName($S), jsonPath($S), " + constraints + ')';
                }
        }

        boolean isDate()
        {
                return FieldType.DATE.equals(field.type);
        }

        private Object[] fieldInitializerArgs()
        {
                final LinkedList<Object> args = new LinkedList<>();
                args.add(fieldClass);
                args.add(canonicalName());
                args.add(field.description);
                args.add(field.property);
                args.add(field.name);
                args.add(required(field));
                args.add(field.name);
                args.add(field.name);
                if (isDate()) {
                        args.add(ClassName.get(DateJsonSerializer.class));
                        switch (field.jsonFormat) {
                                case "":
                                case "#date":
                                        args.add("yyyy-MM-dd");
                                        break;
                                case "#time":
                                        args.add("hh:mm:ss");
                                        break;
                                case "#hour":
                                        args.add("hh:mm");
                                        break;
                                case "#datehour":
                                        args.add("yyyy-MM-dd hh:mm");
                                        break;
                                case "#datetime":
                                        args.add("yyyy-MM-dd hh:mm:ss");
                                        break;
                                case "#timestamp":
                                        args.add("yyyy-MM-dd hh:mm:ss.S");
                                        break;
                                default:
                                        args.add(field.jsonFormat);
                                        break;
                        }
                }
                args.addAll(constraintsArgs(field));
                return args.toArray();
        }

        TypeName mappedFieldClass(final MetaField field)
        {
                if (field.indexed) {
                        return getFieldClass(field, INDEXED_BY_TYPE_NAME);
                } else {
                        return getFieldClass(field, UNINDEXED_BY_TYPE_NAME);
                }
        }

        TypeName getFieldClass(final MetaField field, final ImmutableMap<TypeName, ClassName> fieldClasses)
        {
                if (!fieldClasses.containsKey(field.type)) {
                        throw new ModelException(
                                "No Field known for type [" + field.type + "] used at '" + field.name + "'.");
                }
                return fieldClasses.get(field.type);
        }
}
