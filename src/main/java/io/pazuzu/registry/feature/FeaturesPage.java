package io.pazuzu.registry.feature;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FeaturesPage<F, T> implements Page<T> {

    private final Page<F> page;
    private final Function<F, T> converter;

    public FeaturesPage(Page<F> page, Function<F, T> converter) {
        this.page = page;
        this.converter = converter;
    }

    @Override
    public int getTotalPages() {
        return page.getTotalPages();
    }

    @Override
    public long getTotalElements() {
        return page.getTotalElements();
    }

    @Override
    public <U> Page<U> map(Function<? super T, ? extends U> converter) {
        return page.map(f -> converter.apply(this.converter.apply(f)));
    }

    @Override
    public int getNumber() {
        return page.getNumber();
    }

    @Override
    public int getSize() {
        return page.getSize();
    }

    @Override
    public int getNumberOfElements() {
        return page.getNumberOfElements();
    }

    @Override
    public List<T> getContent() {
        return StreamSupport.stream(page.spliterator(), false)
                .map(converter)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasContent() {
        return page.hasContent();
    }

    @Override
    public Sort getSort() {
        return page.getSort();
    }

    @Override
    public boolean isFirst() {
        return page.isFirst();
    }

    @Override
    public boolean isLast() {
        return page.isLast();
    }

    @Override
    public boolean hasNext() {
        return page.hasNext();
    }

    @Override
    public boolean hasPrevious() {
        return page.hasPrevious();
    }

    @Override
    public Pageable nextPageable() {
        return page.nextPageable();
    }

    @Override
    public Pageable previousPageable() {
        return page.previousPageable();
    }

    @Override
    public Iterator<T> iterator() {
        return StreamSupport.stream(page.spliterator(), false)
                .map(converter)
                .collect(Collectors.toList()).iterator();
    }
}
