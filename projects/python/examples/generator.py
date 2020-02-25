def AlterRangeG(start=0,stop=100):
    while start < stop:
        yield start
        start += 2

# for i in AlterRangeG(5,20):
    # print i        
        
a = AlterRangeG( 5,20 )
print a 
for i in range(15):
    print i,a.next()
