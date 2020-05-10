#!/usr/bin/env python
#encoding utf-8
'''
env.py vm "find vmoptions files and print jvm options"
env.py cp "print classpath"
env.py ps "print tauris processes"
'''
import os
import sys

from distutils.spawn import find_executable

prog = os.path.abspath(sys.argv[0])

HOME = os.environ.get("T_HOME", "/usr/local/tauris")

lib_dir = os.path.join(HOME, "lib")
option_file = os.path.join(HOME, "config", "tauris.jvmoptions")

def make_classpath():
    classpath = lib_dir + "/*"
    cp = os.environ.get('CLASSPATH', "")
    if cp:
        classpath = cp + ":" + classpath
    return classpath

def strip_comment(s):
    if '#' not in s:
        return s.strip()
    rsharp = s.index('#')
    if rsharp >= 0:
        s = s[0:rsharp]
    return s.strip()
    
def read_option_file(filename):
    fp = open(filename)
    options = set([])
    props = {}
    for line in fp.readlines():
        line = strip_comment(line)
        if not line:
            continue
        if line.startswith("-D"):
            ss = line.split("=")
            if len(ss) < 2:
                continue
            props[ss[0]] = "".join(ss[1:])
        elif line.startswith("-X"):
            options.add(line)
    fp.close()
    return props, options

def find_user_option_file(app_name):
    optname = app_name + ".jvmoptions"
    cfg_dirs = []
    cfg_dirs.append(optname)
    cfg_dirs.append(os.path.join('config', optname))
    cfg_dirs.append(os.path.join(os.path.dirname(prog), 'config', optname))        
    cfg_dirs.append(os.path.join(os.path.dirname(prog), os.path.pardir, 'config', optname))
    for d in cfg_dirs:
        #print os.path.abspath(d)
        if os.path.exists(d):
            return os.path.abspath(d)
    return None

def make_jvmoptions(app_name):
    defprops, defoptions = read_option_file(option_file)
    user_option_file = find_user_option_file(app_name)
    if user_option_file:
        userprops, useroptions = read_option_file(user_option_file)
        for opt in useroptions:
            defoptions.add(opt)
        for k, v in userprops.items():
            defprops[k] = v
    vmoptions = []
    for k in defoptions:
        vmoptions.append(k)
    for k,v in defprops.items():
        vmoptions.append("%s=%s" % (k, v))
    return " ".join(vmoptions)
    
def print_classpath(argv):
    print make_classpath()

def print_vmoptions(argv):
    if len(argv) == 0:
        print 'usage %s <app_name> [workdir]' % (prog)
        sys.exit(1)
    app_name = argv[0]
    if len(argv) > 1 and os.path.exists(argv[1]):
        os.chdir(argv[1])
    print make_jvmoptions(app_name)

def print_processes():
    for line in os.popen("ps -ef |grep com.aliyun.tauris.Bootstrap").readlines():
        if '-server' in line:
            print line.strip()

if __name__ == "__main__":
    if len(sys.argv) < 2 or sys.argv[1] not in ('cp', 'vm', 'ps'):
        print "usage: %s <cp|vm|ps>" % sys.argv[0]
        sys.exit(1)
    cmd = sys.argv[1]
    if cmd == 'cp':
        print_classpath(sys.argv[1:])
    if cmd == 'vm':
        print_vmoptions(sys.argv[2:])
    if cmd == 'ps':
        print_processes()
