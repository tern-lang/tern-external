package tern.platform.generate;

import tern.core.type.Type;

public interface ClassGenerator {
   Class generate(Type type, Class base);
}