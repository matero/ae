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
package processor.test;

import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PhoneNumber;
import com.google.appengine.api.datastore.Text;
import java.util.List;
import java.util.stream.Collectors;
import ae.Record;
import com.google.appengine.api.datastore.Key;
import ae.model;

@model
class Competencia extends __Competencia {

    static class R extends Record {

        @id
        String nombre;
    }

    Competencia()
    {
    }
}

@model(kind = "competidores")
public class Competidor extends __Competidor {

    static class R extends Record {

        @id
        long personId;   // Google+ ID of the person
        @parent
        Competencia competencia; // solo para probar que se pueden definir chils models

        @notBlank
        String nombreVisible;  // nombre del competidor, preparado para ser presentado
        @notBlank
        @required
        String nombres;
        @notBlank
        @required
        String apellidos;
        String prefijo;
        String sufijo;
        @notBlank
        @indexed
        String apodo;
        @required
        java.util.Date nacimiento;
        @required
        String sexo; // MASCULINO, FEMENINO, OTRO;
        @indexed
        @property("fono")
        @description("Telefono")
        PhoneNumber telefonoPersonal;
        @property("emergencia")
        @description("Telefono de Emergencia")
        PhoneNumber telefonoEmergencias;
        @required
        @indexed
        Email email;
        Email emailEmergencias;
        @notBlank
        Text info;
        List<Key> participaciones;
    };

    List<Entity> findByApodo(final String valor)
    {
        return selectAll()
                .where(apodo.eq(valor))
                .sortedBy(apodo.asc())
                .limit(10)
                .asList();
    }

    List<String> findNombresByApodo(final String valor)
    {
        return findByApodo(valor).stream()
                .map((data) -> nombres(data))
                .collect(Collectors.toList());
    }
}
