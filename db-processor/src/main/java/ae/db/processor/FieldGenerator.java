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
  private static final ImmutableMap<TypeName, ClassName> INDEXED_BY_TYPE_NAME = ImmutableMap.<TypeName, ClassName>builder()
          .put(FieldType.BLOB_KEY, ClassName.get(BlobKeyField.IndexedBlobKey.class))
          .put(FieldType.BOOLEAN, ClassName.get(IndexedBoolean.class))
          .put(FieldType.CATEGORY, ClassName.get(CategoryField.Indexed.class))
          .put(FieldType.DATE, ClassName.get(DateField.Indexed.class))
          .put(FieldType.DOUBLE, ClassName.get(DoubleField.IndexedDouble.class))
          .put(FieldType.EMAIL, ClassName.get(EmailField.Indexed.class))
          .put(FieldType.GEO_PT, ClassName.get(GeoPtField.Indexed.class))
          .put(FieldType.IM_HANDLE, ClassName.get(IMHandleField.Indexed.class))
          .put(FieldType.KEY, ClassName.get(KeyField.Indexed.class))
          .put(FieldType.LINK, ClassName.get(LinkField.IndexedLink.class))
          .put(FieldType.LONG, ClassName.get(LongField.Indexed.class))
          .put(FieldType.PHONE_NUMBER, ClassName.get(PhoneNumberField.Indexed.class))
          .put(FieldType.POSTAL_ADDRESS, ClassName.get(PostalAddressField.Indexed.class))
          .put(FieldType.RATING, ClassName.get(RatingField.IndexedRating.class))
          .put(FieldType.SHORT_BLOB, ClassName.get(ShortBlobField.IndexedShortBlob.class))
          .put(FieldType.STRING, ClassName.get(StringField.IndexedString.class))
          .put(FieldType.USER, ClassName.get(UserField.IndexedUser.class))
          .put(FieldType.BLOB_KEY_LIST, ClassName.get(BlobKeyListField.IndexedBlobKeyList.class))
          .put(FieldType.BOOLEAN_LIST, ClassName.get(BooleanListField.IndexedBooleanList.class))
          .put(FieldType.CATEGORY_LIST, ClassName.get(CategoryListField.Indexed.class))
          .put(FieldType.DATE_LIST, ClassName.get(DateListField.Indexed.class))
          .put(FieldType.DOUBLE_LIST, ClassName.get(DoubleListField.IndexedDoubleList.class))
          .put(FieldType.EMAIL_LIST, ClassName.get(EmailListField.Indexed.class))
          .put(FieldType.GEO_PT_LIST, ClassName.get(GeoPtListField.Indexed.class))
          .put(FieldType.IM_HANDLE_LIST, ClassName.get(IMHandleListField.Indexed.class))
          .put(FieldType.KEY_LIST, ClassName.get(KeyListField.Indexed.class))
          .put(FieldType.LINK_LIST, ClassName.get(LinkListField.IndexedLinkList.class))
          .put(FieldType.LONG_LIST, ClassName.get(LongListField.Indexed.class))
          .put(FieldType.PHONE_NUMBER_LIST, ClassName.get(PhoneNumberListField.Indexed.class))
          .put(FieldType.POSTAL_ADDRESS_LIST, ClassName.get(PostalAddressListField.Indexed.class))
          .put(FieldType.RATING_LIST, ClassName.get(RatingListField.IndexedRatingList.class))
          .put(FieldType.SHORT_BLOB_LIST, ClassName.get(ShortBlobListField.IndexedShortBlobList.class))
          .put(FieldType.STRING_LIST, ClassName.get(StringListField.IndexedStringList.class))
          .put(FieldType.USER_LIST, ClassName.get(UserListField.IndexedUserList.class))
          .build();

  private static final ImmutableMap<TypeName, ClassName> UNINDEXED_BY_TYPE_NAME = ImmutableMap.<TypeName, ClassName>builder()
          .put(FieldType.BLOB, ClassName.get(UnindexedBlob.class))
          .put(FieldType.BLOB_KEY, ClassName.get(BlobKeyField.UnindexedBlobKey.class))
          .put(FieldType.BOOLEAN, ClassName.get(BooleanField.Unindexed.class))
          .put(FieldType.CATEGORY, ClassName.get(CategoryField.Unindexed.class))
          .put(FieldType.DATE, ClassName.get(DateField.Unindexed.class))
          .put(FieldType.DOUBLE, ClassName.get(DoubleField.UnindexedDouble.class))
          .put(FieldType.EMAIL, ClassName.get(EmailField.Unindexed.class))
          .put(FieldType.EMBEDDED_ENTITY, ClassName.get(UnindexedEmbeddedEntity.class))
          .put(FieldType.GEO_PT, ClassName.get(GeoPtField.Unindexed.class))
          .put(FieldType.IM_HANDLE, ClassName.get(IMHandleField.Unindexed.class))
          .put(FieldType.KEY, ClassName.get(KeyField.Unindexed.class))
          .put(FieldType.LINK, ClassName.get(LinkField.UnindexedLink.class))
          .put(FieldType.LONG, ClassName.get(LongField.Unindexed.class))
          .put(FieldType.PHONE_NUMBER, ClassName.get(PhoneNumberField.Unindexed.class))
          .put(FieldType.POSTAL_ADDRESS, ClassName.get(PostalAddressField.Unindexed.class))
          .put(FieldType.RATING, ClassName.get(RatingField.UnindexedRating.class))
          .put(FieldType.SHORT_BLOB, ClassName.get(ShortBlobField.UnindexedShortBlob.class))
          .put(FieldType.STRING, ClassName.get(StringField.UnindexedString.class))
          .put(FieldType.TEXT, ClassName.get(UnindexedText.class))
          .put(FieldType.USER, ClassName.get(UserField.UnindexedUser.class))
          .put(FieldType.BLOB_LIST, ClassName.get(UnindexedBlobList.class))
          .put(FieldType.BLOB_KEY_LIST, ClassName.get(BlobKeyListField.UnindexedBlobKeyList.class))
          .put(FieldType.BOOLEAN_LIST, ClassName.get(BooleanListField.UnindexedBooleanList.class))
          .put(FieldType.CATEGORY_LIST, ClassName.get(CategoryListField.Unindexed.class))
          .put(FieldType.DATE_LIST, ClassName.get(DateListField.Unindexed.class))
          .put(FieldType.DOUBLE_LIST, ClassName.get(DoubleListField.UnindexedDoubleList.class))
          .put(FieldType.EMAIL_LIST, ClassName.get(EmailListField.Unindexed.class))
          .put(FieldType.EMBEDDED_ENTITY_LIST, ClassName.get(UnindexedEmbeddedEntityList.class))
          .put(FieldType.GEO_PT_LIST, ClassName.get(GeoPtListField.Unindexed.class))
          .put(FieldType.IM_HANDLE_LIST, ClassName.get(IMHandleListField.Unindexed.class))
          .put(FieldType.KEY_LIST, ClassName.get(KeyListField.Unindexed.class))
          .put(FieldType.LINK_LIST, ClassName.get(LinkListField.UnindexedLinkList.class))
          .put(FieldType.LONG_LIST, ClassName.get(LongListField.Unindexed.class))
          .put(FieldType.PHONE_NUMBER_LIST, ClassName.get(PhoneNumberListField.Unindexed.class))
          .put(FieldType.POSTAL_ADDRESS_LIST, ClassName.get(PostalAddressListField.Unindexed.class))
          .put(FieldType.RATING_LIST, ClassName.get(RatingListField.UnindexedRatingList.class))
          .put(FieldType.SHORT_BLOB_LIST, ClassName.get(ShortBlobListField.UnindexedShortBlobList.class))
          .put(FieldType.STRING_LIST, ClassName.get(StringListField.UnindexedStringList.class))
          .put(FieldType.TEXT_LIST, ClassName.get(UnindexedTextList.class))
          .put(FieldType.USER_LIST, ClassName.get(UserListField.UnindexedUserList.class))
          .build();

  private final TypeName fieldClass;
  final MetaField field;

  FieldGenerator(final MetaField field, final ClassName modelClass) {
    super(modelClass);
    this.fieldClass = mappedFieldClass(field);
    this.field = field;
  }

  @Override final String canonicalName() {
    return field.canonicalNameAt(modelClass.toString());
  }

  @Override
  void buildAt(final TypeSpec.Builder modelSpec) {
    modelSpec.addField(
            FieldSpec.builder(fieldClass, field.name, attributeModifiers(field))
                    .initializer(fieldInitializerFormat(), fieldInitializerArgs())
                    .build()
    );
  }

  protected String fieldInitializerFormat() {
    if (isDate()) {
      return "new $T(canonicalName($S), description($S), property($S), field($S), required($L), jsonName($S), jsonPath($S), new $T($S)," + constraints(field) + ')';
    } else {
      return "new $T(canonicalName($S), description($S), property($S), field($S), required($L), jsonName($S), jsonPath($S), " + constraints(field) + ')';
    }
  }

  boolean isDate() {
    return FieldType.DATE.equals(field.type);
  }

  private Object[] fieldInitializerArgs() {
    final LinkedList<Object> args = new LinkedList<>();
    args.add(fieldClass);
    args.add(canonicalName());
    args.add(field.description);
    args.add(field.property);
    args.add(field.name);
    args.add(field.required);
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

  TypeName mappedFieldClass(final MetaField field) {
    if (field.indexed) {
      return getFieldClass(field, INDEXED_BY_TYPE_NAME);
    } else {
      return getFieldClass(field, UNINDEXED_BY_TYPE_NAME);
    }
  }

  TypeName getFieldClass(final MetaField field, final ImmutableMap<TypeName, ClassName> fieldClasses) {
    if (!fieldClasses.containsKey(field.type)) {
      throw new ModelException("No Field known for type [" + field.type + "] used at '" + field.name + "'.");
    }
    return fieldClasses.get(field.type);
  }
}
