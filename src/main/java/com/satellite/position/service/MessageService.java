package com.satellite.position.service;

import com.satellite.position.exception.BeanException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MessageService implements IMessageService {

    @Override
    public String getMessage(List<List<String>> messages) {
        try {
            messages = desfaceMessage(messages);
            List<String> response = new ArrayList<String>();
            List<Word> msgPhrases = new ArrayList<Word>();

            List<Word> finalMsgPhrases = msgPhrases;
            messages.forEach(
                    menssager -> {
                        AtomicInteger i = new AtomicInteger(0);
                        menssager.stream().forEach(msg ->
                                {
                                    if (response.contains(msg)) {
                                        response.add("");
                                        finalMsgPhrases.add(new Word("", i.get()));
                                    } else {
                                        response.add(msg);
                                        finalMsgPhrases.add(new Word(msg, i.get()));
                                    }
                                    i.getAndIncrement();
                                }
                        );
                    }
            );

            msgPhrases = msgPhrases.stream().sorted(Comparator.comparingInt(Word::getOrder)).collect(Collectors.toList());
            msgPhrases = deleteEmptyElemet(msgPhrases);

            return messagesResponse(msgPhrases);
        }catch (Exception e) {
            throw new BeanException("Error getMessage");
        }
    }

    private boolean messagesSize(List<List<String>> messages, int size) {
        for (List<String> m : messages) {
            if (m.size() < size) {
                return false;
            }
        }
        return true;
    }

    private List<List<String>> desfaceMessage(List<List<String>> messages) {
        messages = messages.stream().sorted(Comparator.comparingInt(msg -> -msg.size())).collect(Collectors.toList());
        int size = messages.stream().findFirst().get().size();
        int i = 0;
        for (List<String> m : messages) {
            while (m.size() < size) {
                m = addInitialElemet(m);
            }
            messages.set(i, m);
            i++;
        }
        return messages;
    }

    private List<String> addInitialElemet(List<String> m) {
        List<String> addElemet = new ArrayList<String>();
        addElemet.add("");
        addElemet.addAll(m);
        return addElemet;
    }

    private List<Word> deleteEmptyElemet(List<Word> m) {
        return m.stream().filter(msg -> !msg.getWord().equalsIgnoreCase("")).collect(Collectors.toList());
    }

    private String messagesResponse(List<Word> m) {
        String message = "";
        for (Word word : m) {
            message += word.getWord() + " ";
        }
        return message.trim();
    }

    private class Word {
        String word;
        int order;

        Word(String msg, int order) {
            this.word = msg;
            this.order = order;
        }

        public int getOrder() {
            return this.order;
        }

        public String getWord() {
            return this.word;
        }
    }

}
