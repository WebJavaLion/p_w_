import Vuex from 'vuex'
import Vue from 'vue'

Vue.use(Vuex);

export default new Vuex.Store({

    state: {
        wordGroups: wordGroups,
        userData: userData
    },
    getters: {
        getWordGroups: state => state.wordGroups,
        getWordGroupById: state => id => {
            return state.wordGroups.find(wg => wg.id === id)
        },
        getUserInfo: state => state.userData,
        getWordsForGroup: state => wgId => {
            return state.wordGroups.find(wg => wg.id === wgId).wordList
        }
    },
    mutations: {
        addWordGroupMutation(state, wordGroup) {
            state.wordGroups.push(wordGroup)
        },
        updateWordGroupMutation(state, wordGroup) {
            let index = state.wordGroups.findIndex(wg => wg.id === wordGroup.id);
            state.wordGroups = [
                ...state.wordGroups.slice(0, index),
                wordGroup,
                ...state.wordGroups.slice(index + 1)
            ]
        },
        deleteWordByIdMutation(state, wordId) {
            let wordGroup = state.wordGroups
                .find(wg => wg.wordList.map(w => w.id).includes(wordId));
            let index = wordGroup.wordList.findIndex(w => w.id === wordId);

            wordGroup.wordList = [
                ...wordGroup.wordList.slice(0, index),
                ...wordGroup.wordList.slice(index + 1)
            ];

            let wgIndex = state.wordGroups.findIndex(wordG => wordG.id === wordGroup.id);
            state.wordGroups = [
                ...state.wordGroups.slice(0, wgIndex),
                wordGroup,
                ...state.wordGroups.slice(wgIndex + 1)
            ]
        },
        addWordForGroupMutation(state, word) {
            let wordGroup = state.wordGroups.find(wg => wg.id === word.groupId);
            let index = state.wordGroups.findIndex(wg => wg.id === word.groupId);
            let list = wordGroup.wordList;
            list.push(word);
            wordGroup.wordList = list;

            state.wordGroups = [
                ...state.wordGroups.slice(0, index),
                wordGroup,
                ...state.wordGroups.slice(index + 1)
            ]
        },
        updateWordByIdMutation(state, word) {
            let wordGroup = state.wordGroups.find(wg => wg.id === word.groupId);
            let index = wordGroup.wordList.findIndex(w => w.id === word.id);
            wordGroup.wordList = [
                ...wordGroup.wordList.slice(0, index),
                word,
                ...wordGroup.wordList.slice(index + 1)
            ];
        }
    },
    actions: {
        addWordGroupAction({commit}, wordGroup) {
            Vue.http.post('/api/word/group', wordGroup)
                .then(
                    result => result.json(),
                    error => console.error(error))
                .then(result => commit('addWordGroupMutation', result))
        },
        updateWordGroupAction({commit}, wordGroup) {
            Vue.http.put("/api/word/group", wordGroup)
                .then(
                    result => result.json(),
                    error => console.error(error))
                .then(result => commit('updateWordGroupMutation', result))
        },
        addWordForGroupAction({commit}, word) {
            Vue.http.post('/api/word/', word)
                .then(
                    result => result.json(),
                    error => console.error(error))
                .then(result => commit('addWordForGroupMutation', result))
        },
        deleteWordByIdAction({commit}, id) {
            Vue.http.delete('/api/word/' + id)
                .then(
                    result => commit('deleteWordByIdMutation', id),
                    error => console.error(error))
        },
        updateWordAction({commit}, {word, callback}) {
            let wordF = null;
            this.state.wordGroups.forEach(wordGroup =>
                wordGroup.wordList.forEach(w => {
                    if (w.id === word.id) {
                        wordF = w;
                    }
                }));
            wordF.wordOriginal = word.wordOriginal;
            wordF.wordTranslation = word.wordTranslation;

            Vue.http.put('/api/word/', wordF)
                .then(
                    result => result.json(),
                    error => console.log(error))
                .then(
                    result => commit('updateWordByIdMutation', result),
                )
                .then(res => callback())
        }
    }
})