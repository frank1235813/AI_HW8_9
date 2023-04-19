package aias_lab5.Hw4

import chisel3._
import chisel3.util._

class top extends Module {
    val io = IO(new Bundle{
        val pc_out = Output(UInt(32.W))
        val alu_out = Output(UInt(32.W))
        val rf_wdata_out = Output(UInt(32.W))
        val brtaken_out = Output(Bool())
        val jmptaken_out = Output(Bool())
    })

    val pc = Module(new PC())
    val im = Module(new InstMem())
    val dc = Module(new Decoder())
    val rf = Module(new RegFile(2))
    val alu = Module(new ALU())
    val bc = Module(new BranchComp())
    val dm = Module(new DataMem())
    
    //PC
    pc.io.jmptaken := false.B // Don't modify
    pc.io.brtaken := false.B // Don't modify
    pc.io.offset := 0.U // Don't modify
    
    //Insruction Memory
    im.io.raddr := pc.io.pc
    
    //Decoder
    dc.io.inst := im.io.rdata
    //println(dc.io.inst(0))
    
    //RegFile
    rf.io.raddr(0) := dc.io.rs1
    rf.io.raddr(1) := Mux(dc.io.ctrl_BSel, dc.io.imm.asUInt, 0.U)
    rf.io.wdata := alu.io.out
    
    rf.io.waddr := 0.U  // Don't modify
    rf.io.wen := false.B // Don't modify
    
    //ALU
    val rdata_or_zero = WireDefault(0.U(32.W))
    alu.io.src1 := rf.io.rdata(0)
    alu.io.src2 := Mux(dc.io.ctrl_BSel, rf.io.raddr(1), dc.io.imm.asUInt)
    alu.io.funct3 := dc.io.funct3
    alu.io.funct7 := dc.io.funct7

    //Data Memory
    dm.io.funct3 := dc.io.funct3
    dm.io.raddr := rf.io.raddr(0)
    dm.io.wen := dc.io.ctrl_MemRW
    dm.io.waddr :=  rf.io.raddr(1)
    dm.io.wdata := dc.io.imm.asUInt
    
    //Branch Comparator
    bc.io.en := dc.io.ctrl_Br // need to be changed
    bc.io.funct3 := dc.io.funct3
    bc.io.src1 := rf.io.rdata(0)
    bc.io.src2 := rf.io.rdata(1)


    //Check Ports
    io.pc_out := pc.io.pc
    io.alu_out := alu.io.out 
    io.rf_wdata_out := Mux(dc.io.ctrl_WBSel, rf.io.wdata, dm.io.rdata.asUInt)
    io.brtaken_out := bc.io.brtaken
    io.jmptaken_out := dc.io.ctrl_Jmp // need to be changed
}