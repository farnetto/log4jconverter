import sys
import re
from io import open

lines = open(sys.argv[1],encoding="ISO-8859-1").readlines()

j = "log4j."
t = "log4j.category"
a = "log4j.appender"
f = u'=${fileapp}'

for s in lines:
    skip = False
    s = s.strip()
    s = s.replace(" = ","=")
    if s.startswith(c):
        s = "property." + s[len(c):]
    elif s.startswith(t):
        s = "logger" + s[len(t):]
    elif s.startswith(a):
        s = "appender" + s[len(a):]
    elif s.startswith(j):
        s = "property." + s[len(j):]

    if s.find("FileApp") != -1:
        skip = True
    elif s.find("fileapp") != -1:
        n = s[:s.find("=")]
        s = n + ".type=File"
        print(n+".name="+n[n.find('.')+1:])

    if not skip and s.find("additivity") != -1:
        skip = True

    if not skip and s.find(".layout=") != -1:
        s = s[:s.find("layout")-1] + ".layout.type=PatternLayout"

    if not skip and s.find("CDPatternLayout") != -1:
        skip = True


    if not skip and s.find("logger") != -1:
        skip = True
        g = s[s.find('.')+1:s.find('=')]
        print("logger." + g + ".name=" + g)
        print("logger." + g + ".additivity=false")
        print("logger." + g + ".level" + s[s.find('='):s.find(',')])
        print("logger." + g + ".appenderRef." + g + ".ref=" + s[s.find(',')+2:])
        print

    if not skip:
        s = s.replace("${log4j.","${")
        s = s.replace("basedir}$","basedir}/$")
        s = s.replace(".File=",".fileName=")
        s = s.replace("ConversionPattern","pattern")
        print(s)
