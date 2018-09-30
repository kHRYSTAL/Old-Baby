package com.oldbaby.oblib.retrofit;

import com.google.gson.Gson;
import com.oldbaby.oblib.component.application.OGApplication;
import com.oldbaby.oblib.util.MLog;
import com.squareup.okhttp.ResponseBody;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;

import retrofit.Converter;

final class ResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final Type type;

    ResponseBodyConverter(Gson gson, Type type) {
        this.gson = gson;
        this.type = type;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        Reader reader = value.charStream();
        BufferedReader in = new BufferedReader(reader);
        StringBuffer buffer = new StringBuffer();
        String line;
        while ((line = in.readLine()) != null) {
            buffer.append(line);
        }
        String responseBody = buffer.toString();
        try {
            if (String.class == type) {
                return (T) responseBody;
            }
            return gson.fromJson(responseBody, type);

        } catch (Throwable ex) {
            MLog.e("ResponseConvert", ex.getMessage(), ex);
            return (T) null;
        } finally {
            closeQuietly(reader);
        }
    }

    void closeQuietly(Closeable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (IOException ignored) {
        }
    }
}