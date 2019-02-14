package org.ternlang.platform.generate;

import org.ternlang.core.type.Type;

public interface ClassGenerator {
   Class generate(Type type, Class base);
}