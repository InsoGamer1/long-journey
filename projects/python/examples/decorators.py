import time

def dump_args(func):
    "This decorator dumps out the arguments passed to a function before calling it"
    argnames = func.func_code.co_varnames[:func.func_code.co_argcount]
    fname = func.func_name
    def echo_func(*args,**kwargs):
        print fname, "(", ', '.join(
            '%s=%r' % entry
            for entry in zip(argnames,args[:len(argnames)])+[("args",list(args[len(argnames):]))]+[("kwargs",kwargs)]) +")"
    return echo_func

def dec_time(func):
    def wrapper(*args,**kargs):
        now = time.time()
        ret = func( *args , **kargs )
        print "time taken %s : "%(func.func_name) + str(time.time()-now) + " secs"
        return ret
    return wrapper
    
def dec_time_args(title):
    def inside_fun(func):
        def wrapper(*args,**kargs):
            now = time.time()
            ret = func( *args , **kargs )
            print "%s => time taken %s : "%(title,func.func_name) + str(time.time()-now) + " secs"
            return ret
        return wrapper
    return inside_fun
    
# @dump_args
# @dec_time
@dec_time_args("no.1")
def xyz(a,b):
    print "bfore sleep"
    time.sleep(1)
    print "after slep"
    

xyz(1,2)
