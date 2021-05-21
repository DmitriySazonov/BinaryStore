# BinaryStore

## Сохранение данных в бинарном формате

Основным юнитом является ```BinaryAdapter```. Имеет три основных метода:
1. Определение размера сущности
```java
    int getSize(@Nonnull T value) throws Exception;
```

2. Сереализация сущьности во входной ```ByteBuffer```
```java
    void serialize(@Nonnull ByteBuffer byteBuffer, @Nonnull T value) throws Exception;
```

3. Дезереализация сущьности из выходного ```ByteBuffer```
```java
    @Nonnull T deserialize(@Nonnull ByteBuffer byteBuffer) throws Exception;
```

Использовать адаптер предпологается так:
```java
        val instance: TestClass
        val adapter: BinaryAdapter<TestClass>

        val size = adapter.getSize(response)
        val buffer = StaticByteBuffer(size)
        adapter.serialize(buffer, instance)

        buffer.offset = 0
        
        val deserialized = adapter.deserialize(buffer)
        
        instance == deserialized // при сравнении через equals объекты будут равны
```

## Создание адаптера
Создать адаптер можно вручную либо через кодогенерацию. В ручную предпологается создавать адаптры которые не могут быть сгенерированы 
самостоятельно(напр. коллекции, битмапы, и т.д.) или если проще написать адаптер самостоятельно чем расставлять правила 
для генератора(напр. если в сущьности нужно сохранить лишь несколько полей из большого кол-во).

Что бы для класса был сгенерирован адаптер нужно его поменить его аннотацией.
```java
public @interface Persistable {
    String id(); // id сущьности
    IdType idType() default IdType.STRING; // Тип идентификатора. Может быть INT или STRING. Для лучшей производительности нужно использовать INT
    int versionId() default 1; // В файл сохранится версию и хеш полей сущности для версионности
    InjectType inject() default InjectType.CONSTRUCTOR; // Тип присвоения полей в сущность
}

public enum InjectType {
    CONSTRUCTOR, // Все парамтеры будут переданны через констурктор
    ASSIGNMENT, // Все параметры будут присвоены созданному объекту. Конструктор будет вызван пустой
    AUTO // Ищет наиболее подходящий констурктор, оставшиеся поля устанавливает присвоением
    // Если хочется зафорсить использование конкретного констурктора используется анотация BinaryConstructor
}
```

### Параметры для полей сущности
```java
public @interface Array {
    boolean even(); // говорит о том что многомерный массив имеет одинаковые длинны своих елементов. Это позволяет оптимизировать определение размера массива
}

public @interface Field {
    boolean staticType(); // Говорит о том что инстансе объекта который будт присвоен всегда совпадает с объявленным типом.
    // Позволяет оптимизировать поиск подходящего адаптера
}

public @interface ProvideProperties {
    Class<? extends Property<?>>[] properties(); // Провайдинг зависимостей адаптера которые будут работать с полем
}
```
Пример провайдинга зависимости
```java
@ProvideProperties(properties = {
        MapSettings.SkipItemSettingProperty.class,
})
public Map<String, String> map1 = new HashMap<>();

@ProvideProperties(properties = {
        MapSettings.ThrowExceptionSettingProperty.class,
})
public Map<String, String> map2 = new HashMap<>();


public final static class ThrowExceptionSettingProperty extends AbstractProperty {

  private static final MapSettings settings = new MapSettings(
        UnknownItemStrategy.THROW_EXCEPTION, // unknownItemStrategy
        UnknownItemStrategy.THROW_EXCEPTION // exceptionItemStrategy
  );

  @Override
  public MapSettings provide() {
    return settings;
  }
}
```


## Существующие адаптеры
- BinaryStoreAdapter - Адаптреры для стандартных Java компонентов, например коллекции. Есть ленивые колекции
- BinaryStoreAndroidAdapter - Адаптеры для стандартны Android компонетов.


# RoadMap 
Будет позже
