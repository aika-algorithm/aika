import aika

print(aika.add(1, 2))

print(dir(aika))
#print(hasattr(aika, "TypeRegistry"))

tr = aika.TypeRegistry()

t = aika.Type(tr)

