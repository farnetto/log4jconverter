ls = open('logs.txt').readlines()
for line in ls:
    g = line.strip()
    g = "/d/c186/FarnettoApps/" + g
    print("echo loading %s"%(g))
    print("java -Dserverid=farnetto -cp $CP farnetto.log4jconverter.Loader file:/%s 2>&1 | grep ERROR"%(g))
