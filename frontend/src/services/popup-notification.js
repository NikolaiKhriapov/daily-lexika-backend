import {createStandaloneToast} from '@chakra-ui/react'

const {toast} = createStandaloneToast()

let currentToast = null

const popupNotification = (title, description, status, color) => {
    if (currentToast) {
        toast.close(currentToast)
    }

    currentToast = toast({
        title,
        description,
        status,
        colorScheme: color,
        isClosable: true,
        duration: 4 * 1000,
        position: 'top'
    })
}

export const successNotification = (title, description) => {
    popupNotification(title, description, "success", "gray")
}

export const errorNotification = (title, description) => {
    popupNotification(title, description, "error", "red")
}