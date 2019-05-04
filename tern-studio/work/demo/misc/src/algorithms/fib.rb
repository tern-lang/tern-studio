def fib( fib )
    return  fib  if fib <= 1 
    fib( fib - 1 ) + fib( fib - 2 )
end 
puts fib( 30 )
