package org.ternlang.dx.stock;

public interface ProxyAdapter {
   Object invoke(Object object, Object... list) throws Exception;
}