package com.bjknrt.message.board.assembler

import com.bjknrt.message.board.MbMessageBoard
import com.bjknrt.message.board.vo.Message


fun messageBoardToMessage(messageBoard: MbMessageBoard): Message {
    return Message(
        id = messageBoard.knId,
        messageDatetime = messageBoard.knMessageDatetime,
        content = messageBoard.knContent,
        fromId = messageBoard.knFromId,
        toId = messageBoard.knToId
    )
}