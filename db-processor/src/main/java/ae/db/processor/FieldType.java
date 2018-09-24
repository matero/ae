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

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Category;
import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.IMHandle;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Link;
import com.google.appengine.api.datastore.PhoneNumber;
import com.google.appengine.api.datastore.PostalAddress;
import com.google.appengine.api.datastore.Rating;
import com.google.appengine.api.datastore.ShortBlob;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.users.User;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

interface FieldType {

        ClassName BIG_DECIMAL = ClassName.get(BigDecimal.class);
        ClassName BLOB = ClassName.get(Blob.class);
        ClassName BLOB_KEY = ClassName.get(BlobKey.class);
        ClassName BOOLEAN = ClassName.get(Boolean.class);
        ClassName CATEGORY = ClassName.get(Category.class);
        ClassName DATE = ClassName.get(Date.class);
        ClassName DOUBLE = ClassName.get(Double.class);
        ClassName EMAIL = ClassName.get(Email.class);
        ClassName EMBEDDED_ENTITY = ClassName.get(EmbeddedEntity.class);
        ClassName GEO_PT = ClassName.get(GeoPt.class);
        ClassName IM_HANDLE = ClassName.get(IMHandle.class);
        ClassName KEY = ClassName.get(Key.class);
        ClassName LINK = ClassName.get(Link.class);
        ClassName LIST = ClassName.get(List.class);
        ClassName LONG = ClassName.get(Long.class);
        ClassName PHONE_NUMBER = ClassName.get(PhoneNumber.class);
        ClassName POSTAL_ADDRESS = ClassName.get(PostalAddress.class);
        ClassName RATING = ClassName.get(Rating.class);
        ClassName SHORT_BLOB = ClassName.get(ShortBlob.class);
        ClassName STRING = ClassName.get(String.class);
        ClassName TEXT = ClassName.get(Text.class);
        ClassName USER = ClassName.get(User.class);

        TypeName BIG_DECIMAL_LIST = ParameterizedTypeName.get(LIST, BIG_DECIMAL);
        TypeName BLOB_LIST = ParameterizedTypeName.get(LIST, BLOB);
        TypeName BLOB_KEY_LIST = ParameterizedTypeName.get(LIST, BLOB_KEY);
        TypeName BOOLEAN_LIST = ParameterizedTypeName.get(LIST, BOOLEAN);
        TypeName CATEGORY_LIST = ParameterizedTypeName.get(LIST, CATEGORY);
        TypeName DATE_LIST = ParameterizedTypeName.get(LIST, DATE);
        TypeName DOUBLE_LIST = ParameterizedTypeName.get(LIST, DOUBLE);
        TypeName EMAIL_LIST = ParameterizedTypeName.get(LIST, EMAIL);
        TypeName EMBEDDED_ENTITY_LIST = ParameterizedTypeName.get(LIST, EMBEDDED_ENTITY);
        TypeName GEO_PT_LIST = ParameterizedTypeName.get(LIST, GEO_PT);
        TypeName IM_HANDLE_LIST = ParameterizedTypeName.get(LIST, IM_HANDLE);
        TypeName KEY_LIST = ParameterizedTypeName.get(LIST, KEY);
        TypeName LINK_LIST = ParameterizedTypeName.get(LIST, LINK);
        TypeName LONG_LIST = ParameterizedTypeName.get(LIST, LONG);
        TypeName PHONE_NUMBER_LIST = ParameterizedTypeName.get(LIST, PHONE_NUMBER);
        TypeName POSTAL_ADDRESS_LIST = ParameterizedTypeName.get(LIST, POSTAL_ADDRESS);
        TypeName RATING_LIST = ParameterizedTypeName.get(LIST, RATING);
        TypeName SHORT_BLOB_LIST = ParameterizedTypeName.get(LIST, SHORT_BLOB);
        TypeName STRING_LIST = ParameterizedTypeName.get(LIST, STRING);
        TypeName TEXT_LIST = ParameterizedTypeName.get(LIST, TEXT);
        TypeName USER_LIST = ParameterizedTypeName.get(LIST, USER);
}
