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

import com.google.common.collect.ImmutableMap;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.LinkedList;
import ae.db.BlobField;
import ae.db.BlobKeyField;
import ae.db.BlobKeyListField;
import ae.db.BlobListField;
import ae.db.BooleanField;
import ae.db.BooleanListField;
import ae.db.CategoryField;
import ae.db.CategoryListField;
import ae.db.DateField;
import ae.db.DateListField;
import ae.db.DoubleField;
import ae.db.DoubleListField;
import ae.db.EmailField;
import ae.db.EmailListField;
import ae.db.EmbeddedEntityField;
import ae.db.EmbeddedEntityListField;
import ae.db.GeoPtField;
import ae.db.GeoPtListField;
import ae.db.IMHandleField;
import ae.db.IMHandleListField;
import ae.db.KeyField;
import ae.db.KeyListField;
import ae.db.LinkField;
import ae.db.LinkListField;
import ae.db.LongField;
import ae.db.LongListField;
import ae.db.PhoneNumberField;
import ae.db.PhoneNumberListField;
import ae.db.PostalAddressField;
import ae.db.PostalAddressListField;
import ae.db.RatingField;
import ae.db.RatingListField;
import ae.db.ShortBlobField;
import ae.db.ShortBlobListField;
import ae.db.StringField;
import ae.db.StringListField;
import ae.db.TextField;
import ae.db.TextListField;
import ae.db.UserField;
import ae.db.UserListField;

final class FieldGenerator extends AttributeGenerator {
  private static final ImmutableMap<TypeName, ClassName> INDEXED_BY_TYPE_NAME = ImmutableMap.<TypeName, ClassName>builder()
          .put(FieldType.BLOB_KEY, ClassName.get(BlobKeyField.Indexed.class))
          .put(FieldType.BOOLEAN, ClassName.get(BooleanField.Indexed.class))
          .put(FieldType.CATEGORY, ClassName.get(CategoryField.Indexed.class))
          .put(FieldType.DATE, ClassName.get(DateField.Indexed.class))
          .put(FieldType.DOUBLE, ClassName.get(DoubleField.Indexed.class))
          .put(FieldType.EMAIL, ClassName.get(EmailField.Indexed.class))
          .put(FieldType.GEO_PT, ClassName.get(GeoPtField.Indexed.class))
          .put(FieldType.IM_HANDLE, ClassName.get(IMHandleField.Indexed.class))
          .put(FieldType.KEY, ClassName.get(KeyField.Indexed.class))
          .put(FieldType.LINK, ClassName.get(LinkField.Indexed.class))
          .put(FieldType.LONG, ClassName.get(LongField.Indexed.class))
          .put(FieldType.PHONE_NUMBER, ClassName.get(PhoneNumberField.Indexed.class))
          .put(FieldType.POSTAL_ADDRESS, ClassName.get(PostalAddressField.Indexed.class))
          .put(FieldType.RATING, ClassName.get(RatingField.Indexed.class))
          .put(FieldType.SHORT_BLOB, ClassName.get(ShortBlobField.Indexed.class))
          .put(FieldType.STRING, ClassName.get(StringField.Indexed.class))
          .put(FieldType.USER, ClassName.get(UserField.Indexed.class))
          .put(FieldType.BLOB_KEY_LIST, ClassName.get(BlobKeyListField.Indexed.class))
          .put(FieldType.BOOLEAN_LIST, ClassName.get(BooleanListField.Indexed.class))
          .put(FieldType.CATEGORY_LIST, ClassName.get(CategoryListField.Indexed.class))
          .put(FieldType.DATE_LIST, ClassName.get(DateListField.Indexed.class))
          .put(FieldType.DOUBLE_LIST, ClassName.get(DoubleListField.Indexed.class))
          .put(FieldType.EMAIL_LIST, ClassName.get(EmailListField.Indexed.class))
          .put(FieldType.GEO_PT_LIST, ClassName.get(GeoPtListField.Indexed.class))
          .put(FieldType.IM_HANDLE_LIST, ClassName.get(IMHandleListField.Indexed.class))
          .put(FieldType.KEY_LIST, ClassName.get(KeyListField.Indexed.class))
          .put(FieldType.LINK_LIST, ClassName.get(LinkListField.Indexed.class))
          .put(FieldType.LONG_LIST, ClassName.get(LongListField.Indexed.class))
          .put(FieldType.PHONE_NUMBER_LIST, ClassName.get(PhoneNumberListField.Indexed.class))
          .put(FieldType.POSTAL_ADDRESS_LIST, ClassName.get(PostalAddressListField.Indexed.class))
          .put(FieldType.RATING_LIST, ClassName.get(RatingListField.Indexed.class))
          .put(FieldType.SHORT_BLOB_LIST, ClassName.get(ShortBlobListField.Indexed.class))
          .put(FieldType.STRING_LIST, ClassName.get(StringListField.Indexed.class))
          .put(FieldType.USER_LIST, ClassName.get(UserListField.Indexed.class))
          .build();

  private static final ImmutableMap<TypeName, ClassName> UNINDEXED_BY_TYPE_NAME = ImmutableMap.<TypeName, ClassName>builder()
          .put(FieldType.BLOB, ClassName.get(BlobField.class))
          .put(FieldType.BLOB_KEY, ClassName.get(BlobKeyField.Unindexed.class))
          .put(FieldType.BOOLEAN, ClassName.get(BooleanField.Unindexed.class))
          .put(FieldType.CATEGORY, ClassName.get(CategoryField.Unindexed.class))
          .put(FieldType.DATE, ClassName.get(DateField.Unindexed.class))
          .put(FieldType.DOUBLE, ClassName.get(DoubleField.Unindexed.class))
          .put(FieldType.EMAIL, ClassName.get(EmailField.Unindexed.class))
          .put(FieldType.EMBEDDED_ENTITY, ClassName.get(EmbeddedEntityField.class))
          .put(FieldType.GEO_PT, ClassName.get(GeoPtField.Unindexed.class))
          .put(FieldType.IM_HANDLE, ClassName.get(IMHandleField.Unindexed.class))
          .put(FieldType.KEY, ClassName.get(KeyField.Unindexed.class))
          .put(FieldType.LINK, ClassName.get(LinkField.Unindexed.class))
          .put(FieldType.LONG, ClassName.get(LongField.Unindexed.class))
          .put(FieldType.PHONE_NUMBER, ClassName.get(PhoneNumberField.Unindexed.class))
          .put(FieldType.POSTAL_ADDRESS, ClassName.get(PostalAddressField.Unindexed.class))
          .put(FieldType.RATING, ClassName.get(RatingField.Unindexed.class))
          .put(FieldType.SHORT_BLOB, ClassName.get(ShortBlobField.Unindexed.class))
          .put(FieldType.STRING, ClassName.get(StringField.Unindexed.class))
          .put(FieldType.TEXT, ClassName.get(TextField.class))
          .put(FieldType.USER, ClassName.get(UserField.Unindexed.class))
          .put(FieldType.BLOB_LIST, ClassName.get(BlobListField.class))
          .put(FieldType.BLOB_KEY_LIST, ClassName.get(BlobKeyListField.Unindexed.class))
          .put(FieldType.BOOLEAN_LIST, ClassName.get(BooleanListField.Unindexed.class))
          .put(FieldType.CATEGORY_LIST, ClassName.get(CategoryListField.Unindexed.class))
          .put(FieldType.DATE_LIST, ClassName.get(DateListField.Unindexed.class))
          .put(FieldType.DOUBLE_LIST, ClassName.get(DoubleListField.Unindexed.class))
          .put(FieldType.EMAIL_LIST, ClassName.get(EmailListField.Unindexed.class))
          .put(FieldType.EMBEDDED_ENTITY_LIST, ClassName.get(EmbeddedEntityListField.class))
          .put(FieldType.GEO_PT_LIST, ClassName.get(GeoPtListField.Unindexed.class))
          .put(FieldType.IM_HANDLE_LIST, ClassName.get(IMHandleListField.Unindexed.class))
          .put(FieldType.KEY_LIST, ClassName.get(KeyListField.Unindexed.class))
          .put(FieldType.LINK_LIST, ClassName.get(LinkListField.Unindexed.class))
          .put(FieldType.LONG_LIST, ClassName.get(LongListField.Unindexed.class))
          .put(FieldType.PHONE_NUMBER_LIST, ClassName.get(PhoneNumberListField.Unindexed.class))
          .put(FieldType.POSTAL_ADDRESS_LIST, ClassName.get(PostalAddressListField.Unindexed.class))
          .put(FieldType.RATING_LIST, ClassName.get(RatingListField.Unindexed.class))
          .put(FieldType.SHORT_BLOB_LIST, ClassName.get(ShortBlobListField.Unindexed.class))
          .put(FieldType.STRING_LIST, ClassName.get(StringListField.Unindexed.class))
          .put(FieldType.TEXT_LIST, ClassName.get(TextListField.class))
          .put(FieldType.USER_LIST, ClassName.get(UserListField.Unindexed.class))
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
    return "new $T(canonicalName($S), description($S), property($S), field($S), required($L), jsonName($S), jsonPath($S), " + constraints(field) + ')';
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
