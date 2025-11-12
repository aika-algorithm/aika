import aika

print(aika.fields.add(1, 2))

print(dir(aika))
print(hasattr(aika, "TypeRegistry"))

tr = aika.fields.TypeRegistry()

t = aika.fields.Type(tr)

