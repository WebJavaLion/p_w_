<template>
    <v-col cols="12" sm="12">
        <v-card class="pa-2" color="grey lighten-2">
            <h4>{{group.name}}</h4>
            <v-row justify="start">
                <v-col cols="12" sm="3">
                    <v-text-field v-model="currentWord.wordOriginal"
                                  label="original" class="ml-2">
                    </v-text-field>
                </v-col>
                <v-col cols="12" sm="3">
                    <v-text-field v-model="currentWord.wordTranslation"
                                  label="translation" class="ml-2">
                    </v-text-field>
                </v-col>
            </v-row>
            <v-row>
                <v-col cols="12" sm="2" md="1" class="pt-0 ml-2">
                    <v-btn small color="green"
                           :disabled="!isBothFieldsFilled"
                           bottom
                           v-on:click="addWordAndClearForm">
                        submit
                    </v-btn>
                </v-col>
                <v-col cols="12" sm="2" md="1" class="pt-0 ml-2">
                    <v-btn small color="red"
                           :disabled="!isBothFieldsFilled"
                           bottom
                           v-on:click="clearForm">
                        clear
                    </v-btn>
                </v-col>
            </v-row>
            <WordList :word-list="group.wordList"></WordList>
        </v-card>
    </v-col>
</template>


<script>
    import WordList from "./WordList.vue";
    import {mapActions, mapMutations} from "vuex";

    export default {
        data() {
            return {
                group: this.$store.getters.getWordGroupById(this.groupId),
                currentWord: {
                    groupId: this.groupId,
                    wordOriginal: '',
                    wordTranslation: '',
                },
                file: null
            }
        },
        components: {WordList},
        props: ['groupId'],
        computed: {
            isBothFieldsFilled() {
                return this.currentWord.wordOriginal.length > 0 && this.currentWord.wordTranslation.length > 0
            }
        },
        methods: {
            ...mapActions({
                addNewWord: 'addWordForGroupAction'
            }),
            addWordAndClearForm() {
                this.addNewWord(this.currentWord);
                this.currentWord.wordOriginal = '';
                this.currentWord.wordTranslation = ''
            },
            clearForm() {
                this.currentWord.wordOriginal = '';
                this.currentWord.wordTranslation = ''
            }
        },
    }
</script>


<style>


</style>