package kz.jug.saga.sail.control

import kz.jug.saga.sail.entity.Sail

import java.util.function.Consumer

class FakeAction implements Consumer<Sail> {
    @Override
    void accept(Sail sail) {}
}