package me.kingtux.tmvc.core.controller;

import me.kingtux.tmvc.core.Website;
import me.kingtux.tmvc.core.annotations.Path;
import me.kingtux.tmvc.core.request.Request;
import me.kingtux.tmvc.core.request.RequestType;
import me.kingtux.tmvc.core.view.View;
import me.kingtux.tmvc.core.view.ViewManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SingleController {
    private Method method;
    private Object cl;

    public SingleController(Object c, Method method) {
        this.method = method;
        cl = c;
    }

    public String getPath() {
        return method.getAnnotation(Path.class).path();
    }

    public RequestType getRequestType() {
        return method.getAnnotation(Path.class).requestType();
    }

    public String getTemplate() {
        return method.getAnnotation(Path.class).template();
    }

    @SuppressWarnings("All")
    public ControllerExecutor buildExecutor(Request request, ViewManager vb, Website website) {
        return () -> {
            request.setResponseHeader("Access-Control-Allow-Origin", website.getCORS());
            View view = vb.buildView(website, request);
            view.setTemplate(getTemplate());
            try {
                method.invoke(cl, request.getArguments(method.getParameters(), view));
            } catch (InvocationTargetException e) {
                throw new ControllerExeception(this, e.getCause());
            } catch (Throwable e) {
                throw new ControllerExeception(this, e);
            }
            if (!request.hasResponded() || !view.getTemplate().equals(""))
                request.respond(view, vb);
        };
    }

    public String getName() {
        return method.getName();
    }
}
