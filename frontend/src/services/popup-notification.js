import {createStandaloneToast} from '@chakra-ui/react'

const {toast} = createStandaloneToast()

const popupNotification = (title, description, status) => {
    toast({
        title,
        description,
        status,
        isClosable: true,
        duration: 4 * 1000
    })
}

export const successNotification = (title, description) => {
    popupNotification(title, description, "success")
}

export const errorNotification = (title, description) => {
    popupNotification(title, description, "error")
}