<template>
    <transition-group v-bind:css="false"
                      v-on:leave="leave"
                      v-on:before-enter="beforeEnter"
                      v-on:enter="enter"
                      v-on:after-enter="afterEnter"
                      name="group">
        <Word v-for="word in wordList"
              :key="word.id"
              :id="word.id"
              :wordOriginal="word.wordOriginal"
              :wordTranslation="word.wordTranslation">
        </Word>

    </transition-group>
</template>

<script>
    import Word from "./Word.vue";
    import anime from 'animejs/lib/anime.es.js'

    export default {
        props: ['wordList'],
        data() {
            return {}
        },
        components: {Word},
        methods: {
            leave(el, done) {
                anime({
                    targets: el,
                    height: 0,
                    duration: 500,
                    easing: 'easeOutQuad',
                    opacity: [1, 0],
                    complete: done
                });
            },
            beforeEnter(el, done) {
                anime({
                    targets: el,
                    opacity: 0,
                    done: done
                })
            },
            enter(el, done) {
                anime({
                    targets: el,
                    opacity: [0, 1],
                    easing: 'easeInOutQuad',
                    duration: 500,
                    complete: done
                })
            },
            afterEnter(el, done) {
                anime({
                    targets: el,
                    opacity: 1,
                    done: done
                })
            }
        }
    }
</script>

<style>

    .group-leave-active, .group-enter-active {
        position: absolute;

        /*transform: translateY(-200px);*/
    }

    .group-leave-to, .group-enter {
        height: 0;
        opacity: 0;

    }

    .group-move {
        transition: all 1s;
    }

</style>