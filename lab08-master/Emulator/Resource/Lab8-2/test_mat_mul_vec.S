.v.data
A:
.byte 0 1 2 3 4 5 0 0

B:
.byte 0 1 2 0 0 0 0 0
.byte 3 4 5 0 0 0 0 0

C:
.byte 0 0 0 0 0 0 0 0
.byte 0 0 0 0 0 0 0 0
.byte 0 0 0 0 0 0 0 0

.text
la s0,A
la s1,B
la s2,C

li a0,3
li a1,2
li a2,3

li t1,0

loop_i:
bge t1,a0,end
li t0,0
jal loop_k
addi t1,t1,1
j loop_i

loop_k:
bge t0,a1,return
mul t2,t1,a1
add t2,t2,t0
add t2,s0,t2
lb t2,0(t2)

addi t3,x0,8
mul t3,t0,t3
add t3,t3,s1
vle8.v v1,(t3),0
vmul.vx v1,v1,t2,0

addi t3,x0,8
mul t3,t1,t3
add t3,t3,s2
vle8.v v2,(t3),0
vadd.vv v2,v1,v2,0
vse8.v v2,(t3),0

addi t0,t0,1
j loop_k

return:
ret

end:
vle8.v v20,(s2),0
addi s2,s2,4
vle8.v v21,(s2),0
addi s2,s2,4
vle8.v v22,(s2),0
addi s2,s2,-8
hcf
