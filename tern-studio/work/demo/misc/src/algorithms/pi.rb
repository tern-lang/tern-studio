#
# ruby pi - how to calculate pi with ruby.
# proving that pi is the limit of this series:
# 4/1 - 4/3 + 4/5 - 4/7 + 4/9 ...
#
num = 4.0
pi = 0
plus = true

den = 1
while den < 100000000
  if plus 
    pi = pi + num/den
    plus = false
  else
    pi = pi - num/den
    plus = true
  end
  den = den + 2
end

puts "PI = #{pi}"              # calculated value of pi
puts "Math::PI = #{Math::PI}"  # pi from the math class
