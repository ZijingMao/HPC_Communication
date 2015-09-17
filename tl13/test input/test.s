	.data
newline:	.asciiz "\n"
	.text
	.globl main
main:	
	li $fp, 0x7ffffffc
	  
B0:	
	  
	# loadI 0 r_X
	li $t0, 0
	sw $t0, 0($fp)
	  
	# loadI 0 r_Y
	li $t0, 0
	sw $t0, -4($fp)
	  
	# loadI 0 r_Z
	li $t0, 0
	sw $t0, -8($fp)
	  
	# loadI 39 r0
	li $t0, 39
	sw $t0, -12($fp)
	  
	# i2i r0 r_X
	lw $t0, -12($fp)
	add $t0, $t0, $zero
	sw $t0, 0($fp)
	  
	# loadI 97 r1
	li $t0, 97
	sw $t0, -16($fp)
	  
	# i2i r1 r_Y
	lw $t0, -16($fp)
	add $t0, $t0, $zero
	sw $t0, -4($fp)
	  
	# i2i r_Z r2
	lw $t0, -8($fp)
	add $t0, $t0, $zero
	sw $t0, -20($fp)
	  
	# loadI 1 r3
	li $t0, 1
	sw $t0, -24($fp)
	  
	# add r2,r3 r4
	lw $t0, -20($fp)
	lw $t1, -24($fp)
	addu $t0, $t0, $t1
	sw $t0, -28($fp)
	  
	# i2i r4 r_Z
	lw $t0, -28($fp)
	add $t0, $t0, $zero
	sw $t0, -8($fp)
	  
	# exit  
	li $v0, 10
	syscall  
