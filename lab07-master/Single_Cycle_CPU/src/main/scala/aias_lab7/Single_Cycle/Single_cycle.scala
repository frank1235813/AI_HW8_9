package aias_lab7.Single_Cycle

import chisel3._
import chisel3.util._

import aias_lab7.Single_Cycle.WB_select_map._
import aias_lab7.Memory._
import aias_lab7.Single_Cycle.Controller._
import aias_lab7.Single_Cycle.Datapath._

class Single_Cycle extends Module {
    val io = IO(new Bundle{
        //InstMem
        val rinst 	= Input(UInt(32.W))
		val pc 		= Output(UInt(15.W))
        
        //DataMem
		val rdata 	= Input(UInt(32.W))
		
        val raddr 	= Output(UInt(15.W))
        val wdata 	= Output(UInt(32.W))
        val waddr 	= Output(UInt(15.W))		
		val Mem_R  	= Output(Bool())
        val Mem_W  	= Output(Bool())
        val funct3 	= Output(UInt(3.W))

        //System
        val regs 	= Output(Vec(32,UInt(32.W)))
        val Hcf 	= Output(Bool())
    })
    
    //Module
    val ct = Module(new Controller())
    val pc = Module(new PC())
    val ig = Module(new ImmGen())
    val rf = Module(new RegFile(2))
    val alu = Module(new ALU())
    val bc = Module(new BranchComp())

    //wire
    val rd        = Wire(UInt(5.W))
    val rs1       = Wire(UInt(5.W))
    val rs2       = Wire(UInt(5.W))
    val funct3    = Wire(UInt(3.W))
    val inst_31_7 = Wire(UInt(25.W))

    rd  := io.rinst(11,7)
    rs1 := io.rinst(19,15)
    rs2 := io.rinst(24,20)
    funct3 := io.rinst(14,12)
    inst_31_7 := io.rinst(31,7)
    
    //PC
    pc.io.PCSel := ct.io.PCSel
    pc.io.alu_out := Cat(alu.io.out(31, 2), 0.U(2.W))
    pc.io.Hcf := ct.io.Hcf
    
    //Insruction Memory
    io.pc := pc.io.pc

    //ImmGen
    ig.io.ImmSel := ct.io.ImmSel
    ig.io.inst_31_7 := inst_31_7
    
    //RegFile
    rf.io.raddr(0) := rs1
    rf.io.raddr(1) := rs2
    rf.io.waddr := rd
    rf.io.wen := ct.io.RegWEn
    
    when(ct.io.WBSel === LD_DATA_SIG){rf.io.wdata := io.rdata} //from DataMemory
    .elsewhen(ct.io.WBSel === ALUOUT_SIG){rf.io.wdata := alu.io.out} //from ALU
    .elsewhen(ct.io.WBSel === PC_PLUS_4_SIG){rf.io.wdata := pc.io.pc + 4.U} //from PC (+4)
    .otherwise{rf.io.wdata := 0.U} // Default

    //ALU
    alu.io.src1 := MuxLookup(ct.io.ASel, rf.io.rdata(0), Seq(
					1.U -> pc.io.pc,
					2.U -> 0.U(32.W)
	))
    alu.io.src2 := Mux(ct.io.BSel.asBool, ig.io.imm, rf.io.rdata(1))
    alu.io.ALUSel := ct.io.ALUSel
    
    //Data Memory
    io.funct3 := funct3
    io.raddr := alu.io.out(14,0)
	io.Mem_R := ct.io.Mem_R
    io.Mem_W := ct.io.Mem_W
    io.waddr := alu.io.out(14,0)
    io.wdata := rf.io.rdata(1)
    
    //Branch Comparator
    bc.io.BrUn := ct.io.BrUn
    bc.io.src1 := rf.io.rdata(0)
    bc.io.src2 := rf.io.rdata(1)

    //Controller
    ct.io.Inst := io.rinst
    ct.io.BrEq := bc.io.BrEq
    ct.io.BrLT := bc.io.BrLT

    //System
    io.regs := rf.io.regs
    io.Hcf := ct.io.Hcf
}