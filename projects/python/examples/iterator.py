class AlterRange:
    def __init__(self,start=0,stop=100):
        self.start = start
        self.stop = stop
    def __iter__(self):
        return self
    def next(self):
        if self.start > self.stop:
            raise StopIteration
        x = self.start
        self.start += 2
        return x
    
for i in AlterRange(5,20):
    print i
